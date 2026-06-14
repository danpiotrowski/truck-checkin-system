package truckcheckin;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 * Handles CSV uploads from the shipping office.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class CsvUploadController {

    private final LoadRepository loadRepository;
    private final LoadItemRepository loadItemRepository;

    public CsvUploadController(
            LoadRepository loadRepository,
            LoadItemRepository loadItemRepository) {

        this.loadRepository = loadRepository;
        this.loadItemRepository = loadItemRepository;
    }

    /*
     * Upload endpoint:
     *
     * POST /api/uploads/loads
     *
     * Expects:
     * - file: CSV file
     * - scheduledPickupDate: date selected by shipper
     */
    @PostMapping("/api/uploads/loads")
    public String uploadLoads(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scheduledPickupDate") String scheduledPickupDateText)
            throws Exception {

        LocalDate scheduledPickupDate = LocalDate.parse(scheduledPickupDateText);

        int loadsCreated = 0;
        int itemsCreated = 0;

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (
                InputStreamReader reader =
                        new InputStreamReader(file.getInputStream());

                CSVParser parser =
                        new CSVParser(reader, format)
        ) {
            for (CSVRecord record : parser) {

                String loadNumber = cleanCsvValue(record.get("Externally Planned Load Nbr"));
                String orderNumber = cleanCsvValue(record.get("Order Nbr"));
                String itemCode = cleanCsvValue(record.get("Item Code"));
                String batchNumber = cleanCsvValue(record.get("Batch Nbr"));
                String itemDescription = cleanCsvValue(record.get("Item Description"));
                String orderedQtyText = cleanCsvValue(record.get("Ordered Qty"));

                if (loadNumber.isBlank()) {
                    continue;
                }

                Optional<Load> existingLoad =
                        loadRepository.findByLoadNumberAndScheduledPickupDate(
                                loadNumber,
                                scheduledPickupDate);

                Load load;

                if (existingLoad.isPresent()) {
                    load = existingLoad.get();
                } else {
                    load = new Load();
                    load.setLoadNumber(loadNumber);
                    load.setScheduledPickupDate(scheduledPickupDate);
                    load.setStatus("NOT_ARRIVED");
                    load.setActive(true);

                    load = loadRepository.save(load);
                    loadsCreated++;
                }

                String itemBin = itemCode + "-" + batchNumber;
                int pallets = calculatePallets(orderedQtyText, itemDescription);

                Optional<LoadItem> existingItem =
                        loadItemRepository.findByLoadIdAndOrderNumberAndItemBin(
                                load.getId(),
                                orderNumber,
                                itemBin);

                if (existingItem.isEmpty()) {
                    LoadItem item = new LoadItem();
                    item.setLoadId(load.getId());
                    item.setOrderNumber(orderNumber);
                    item.setItemBin(itemBin);
                    item.setItemDescription(itemDescription);
                    item.setPallets(pallets);

                    loadItemRepository.save(item);
                    itemsCreated++;
                }
            }
        }

        return "Upload complete. Loads created: " + loadsCreated
                + ", items created: " + itemsCreated;
    }

    /*
     * Cleans CSV values like:
     *
     * ="400741563"
     *
     * into:
     *
     * 400741563
     */
    private String cleanCsvValue(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replace("=\"", "")
                .replace("\"", "");
    }

    /*
     * Pallet calculation rule:
     *
     * If item description contains S7:
     * pallets = ordered qty / 44
     *
     * Otherwise:
     * pallets = ordered qty / 28
     *
     * Java integer division automatically rounds down.
     */
    private int calculatePallets(String orderedQtyText, String itemDescription) {
        int orderedQty = Integer.parseInt(orderedQtyText);

        if (itemDescription != null && itemDescription.contains("S7")) {
            return orderedQty / 44;
        }

        return orderedQty / 28;
    }
}