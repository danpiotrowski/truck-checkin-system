import { useEffect, useState } from 'react';
import './App.css';

function App() {
  /*
   * loads stores the dashboard rows that come from:
   * GET /api/dashboard/loads
   *
   * These rows combine:
   * - load data
   * - driver check-in data
   */
  const [loads, setLoads] = useState([]);

  /*
   * formData stores everything the driver types into the check-in form.
   */
  const [formData, setFormData] = useState({
    loadId: '',
    driverFirstName: '',
    driverLastName: '',
    truckingCompany: '',
    phoneNumber: '',
    trailerNumber: ''
  });

  /*
   * message shows feedback for the driver check-in form.
   */
  const [message, setMessage] = useState('');

  /*
   * uploadDate stores the scheduled pickup date selected by the shipper.
   */
  const [uploadDate, setUploadDate] = useState('');

  /*
   * uploadFile stores the CSV file selected by the shipper.
   */
  const [uploadFile, setUploadFile] = useState(null);

  /*
   * uploadMessage shows feedback after CSV upload.
   */
  const [uploadMessage, setUploadMessage] = useState('');

  /*
   * Loads dashboard data from Spring Boot.
   *
   * We put this in its own function so we can call it:
   * - when the page first loads
   * - after a driver check-in
   * - after a CSV upload
   */
  function loadDashboardData() {
    fetch('http://localhost:8080/api/dashboard/loads')
      .then(response => response.json())
      .then(data => setLoads(data))
      .catch(error => console.error('Error loading dashboard data:', error));
  }

  /*
   * Runs once when the React page first loads.
   */
  useEffect(() => {
    loadDashboardData();
  }, []);

  /*
   * Handles typing in the driver check-in form.
   */
  function handleChange(event) {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value
    });
  }

  /*
   * Handles the driver check-in form submit.
   */
  function handleSubmit(event) {
    event.preventDefault();

    fetch('http://localhost:8080/api/checkins', {
      method: 'POST',

      headers: {
        'Content-Type': 'application/json'
      },

      body: JSON.stringify({
        ...formData,
        loadId: Number(formData.loadId)
      })
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(
            'This load has already been checked in. Please see the shipping office.'
          );
        }

        return response.json();
      })
      .then(data => {
        setMessage(
          `Check-in submitted for ${data.driverFirstName} ${data.driverLastName}`
        );

        setFormData({
          loadId: '',
          driverFirstName: '',
          driverLastName: '',
          truckingCompany: '',
          phoneNumber: '',
          trailerNumber: ''
        });

        /*
         * Refresh the dashboard after successful check-in
         * so the shipper can see updated driver data/status.
         */
        loadDashboardData();
      })
      .catch(error => {
        console.error('Error submitting check-in:', error);
        setMessage(error.message);
      });
  }

  /*
   * Handles CSV file selection.
   */
  function handleFileChange(event) {
    setUploadFile(event.target.files[0]);
  }

  /*
   * Handles the shipper CSV upload form.
   */
  function handleUploadSubmit(event) {
    event.preventDefault();

    /*
     * Basic validation before sending anything to Spring Boot.
     */
    if (!uploadDate || !uploadFile) {
      setUploadMessage('Please select a pickup date and CSV file.');
      return;
    }

    /*
     * FormData is used when sending files.
     *
     * This creates the same kind of multipart request
     * we tested earlier with curl.exe.
     */
    const uploadFormData = new FormData();

    uploadFormData.append('file', uploadFile);
    uploadFormData.append('scheduledPickupDate', uploadDate);

    fetch('http://localhost:8080/api/uploads/loads', {
      method: 'POST',
      body: uploadFormData
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('CSV upload failed.');
        }

        return response.text();
      })
      .then(data => {
        /*
         * Spring Boot currently returns plain text like:
         * Upload complete. Loads created: 9, items created: 30
         */
        setUploadMessage(data);

        /*
         * Clear the selected file after successful upload.
         */
        setUploadFile(null);

        /*
         * Refresh the dashboard so imported loads appear immediately.
         */
        loadDashboardData();
      })
      .catch(error => {
        console.error('Error uploading CSV:', error);
        setUploadMessage(error.message);
      });
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Truck Check-In System</h1>

      <section className="form-card">
        <h2>Driver Check-In</h2>

        <form onSubmit={handleSubmit}>
          <label>
            Load Database ID
            <input
              type="text"
              name="loadId"
              value={formData.loadId}
              onChange={handleChange}
              required
            />
          </label>

          <p className="field-note">
            Temporary: use the ID from the dashboard table.
          </p>

          <label>
            First Name
            <input
              type="text"
              name="driverFirstName"
              value={formData.driverFirstName}
              onChange={handleChange}
              required
            />
          </label>

          <label>
            Last Name
            <input
              type="text"
              name="driverLastName"
              value={formData.driverLastName}
              onChange={handleChange}
              required
            />
          </label>

          <label>
            Trucking Company
            <input
              type="text"
              name="truckingCompany"
              value={formData.truckingCompany}
              onChange={handleChange}
              required
            />
          </label>

          <label>
            Phone Number
            <input
              type="text"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
            />
          </label>

          <label>
            Trailer Number
            <input
              type="text"
              name="trailerNumber"
              value={formData.trailerNumber}
              onChange={handleChange}
              required
            />
          </label>

          <button type="submit">Check In</button>
        </form>

        {message && <p className="message">{message}</p>}
      </section>

      <section className="form-card">
        <h2>CSV Load Upload</h2>

        <form onSubmit={handleUploadSubmit}>
          <label>
            Scheduled Pickup Date
            <input
              type="date"
              value={uploadDate}
              onChange={event => setUploadDate(event.target.value)}
              required
            />
          </label>

          <label>
            CSV File
            <input
              type="file"
              accept=".csv"
              onChange={handleFileChange}
              required
            />
          </label>

          <button type="submit">Upload CSV</button>
        </form>

        {uploadMessage && <p className="message">{uploadMessage}</p>}
      </section>

      <section>
        <h2>Shipping Dashboard</h2>

        <table className="loads-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Load Number</th>
              <th>Driver</th>
              <th>Company</th>
              <th>Trailer</th>
              <th>Phone</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {loads.map(load => (
              <tr key={load.loadId}>
                <td>{load.loadId}</td>
                <td>{load.loadNumber}</td>

                <td>
                  {load.driverFirstName
                    ? `${load.driverFirstName} ${load.driverLastName}`
                    : 'Not Checked In'}
                </td>

                <td>{load.truckingCompany || '-'}</td>
                <td>{load.trailerNumber || '-'}</td>
                <td>{load.phoneNumber || '-'}</td>
                <td>{load.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default App;