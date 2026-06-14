import { useEffect, useState } from 'react';
import './App.css';

function App() {
  /*
   * loads stores the dashboard rows that come from:
   *
   * GET /api/dashboard/loads
   *
   * These rows combine:
   * - load data
   * - driver check-in data
   */
  const [loads, setLoads] = useState([]);

  /*
   * formData stores everything the driver types into
   * the driver check-in form.
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
   * message shows feedback after the driver submits
   * the check-in form.
   */
  const [message, setMessage] = useState('');

  /*
   * uploadDate stores the scheduled pickup date selected
   * by the shipper during CSV upload.
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
   * dashboardDate stores the pickup date selected
   * by the shipper for filtering the dashboard.
   *
   * Blank value = show all active loads.
   * Date value = show only loads for that pickup date.
   */
  const [dashboardDate, setDashboardDate] = useState('');

  /*
   * Loads dashboard data from Spring Boot.
   *
   * If selectedDate is provided, the backend filters by pickup date.
   * If selectedDate is blank, the backend returns all active loads.
   */
  function loadDashboardData(selectedDate = dashboardDate) {
    let url = 'http://localhost:8080/api/dashboard/loads';

    /*
     * Add a query parameter when the dashboard is filtered by date.
     *
     * Example:
     * http://localhost:8080/api/dashboard/loads?date=2026-06-10
     */
    if (selectedDate) {
      url = `${url}?date=${selectedDate}`;
    }

    fetch(url)
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
   *
   * The input name must match the field name in formData.
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
    /*
     * Prevent the browser from refreshing the page.
     */
    event.preventDefault();

    fetch('http://localhost:8080/api/checkins', {
      method: 'POST',

      /*
       * Tell Spring Boot that we are sending JSON.
       */
      headers: {
        'Content-Type': 'application/json'
      },

      /*
       * Convert the JavaScript object into JSON text.
       *
       * loadId starts as text because HTML input values are strings.
       * Number(formData.loadId) converts it into a number before sending it.
       */
      body: JSON.stringify({
        ...formData,
        loadId: Number(formData.loadId)
      })
    })
      .then(response => {
        /*
         * response.ok is true for HTTP 200–299.
         *
         * If false, the backend rejected the request.
         * Example: duplicate driver check-in.
         */
        if (!response.ok) {
          throw new Error(
            'This load has already been checked in. Please see the shipping office.'
          );
        }

        return response.json();
      })
      .then(data => {
        /*
         * Show a success message using data returned by Spring Boot.
         */
        setMessage(
          `Check-in submitted for ${data.driverFirstName} ${data.driverLastName}`
        );

        /*
         * Clear the driver check-in form.
         */
        setFormData({
          loadId: '',
          driverFirstName: '',
          driverLastName: '',
          truckingCompany: '',
          phoneNumber: '',
          trailerNumber: ''
        });

        /*
         * Refresh the dashboard so the shipper can see
         * the updated driver and load status.
         */
        loadDashboardData();
      })
      .catch(error => {
        console.error('Error submitting check-in:', error);
        setMessage(error.message);
      });
  }

  /*
   * Stores the CSV file selected by the shipper.
   */
  function handleFileChange(event) {
    setUploadFile(event.target.files[0]);
  }

  /*
   * Handles the CSV upload form.
   */
  function handleUploadSubmit(event) {
    /*
     * Prevent the browser from refreshing the page.
     */
    event.preventDefault();

    /*
     * Basic validation before sending the request.
     */
    if (!uploadDate || !uploadFile) {
      setUploadMessage('Please select a pickup date and CSV file.');
      return;
    }

    /*
     * FormData is required for file uploads.
     *
     * This creates a multipart request like the one
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

        /*
         * Spring Boot currently returns plain text:
         * Upload complete. Loads created: X, items created: Y
         */
        return response.text();
      })
      .then(data => {
        setUploadMessage(data);

        /*
         * Clear the stored file after upload.
         */
        setUploadFile(null);

        /*
         * After uploading a CSV, automatically filter
         * the dashboard to the pickup date that was uploaded.
         */
        setDashboardDate(uploadDate);
        loadDashboardData(uploadDate);
      })
      .catch(error => {
        console.error('Error uploading CSV:', error);
        setUploadMessage(error.message);
      });
  }

  /*
   * Runs when the shipper changes the dashboard date filter.
   */
  function handleDashboardDateChange(event) {
    const selectedDate = event.target.value;

    setDashboardDate(selectedDate);

    /*
     * Pass selectedDate directly because React state updates
     * do not happen instantly.
     */
    loadDashboardData(selectedDate);
  }

  /*
   * Clears the dashboard date filter and shows all active loads.
   */
  function clearDashboardDateFilter() {
    setDashboardDate('');
    loadDashboardData('');
  }

  /*
   * Converts internal status values into user-friendly text.
   *
   * Database value: NOT_ARRIVED
   * Display value:  Not Arrived
   */
  function formatStatus(status) {
    switch (status) {
      case 'NOT_ARRIVED':
        return 'Not Arrived';
      case 'WAITING':
        return 'Waiting';
      case 'ASSIGNED_TO_DOOR':
        return 'Assigned to Door';
      case 'COMPLETED':
        return 'Completed';
      default:
        return status || '-';
    }
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

        <div className="dashboard-filter">
          <label>
            Filter by Pickup Date
            <input
              type="date"
              value={dashboardDate}
              onChange={handleDashboardDateChange}
            />
          </label>

          <button type="button" onClick={clearDashboardDateFilter}>
            Show All
          </button>
        </div>

        <table className="loads-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Pickup Date</th>
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
                <td>{load.scheduledPickupDate || '-'}</td>
                <td>{load.loadNumber}</td>

                <td>
                  {load.driverFirstName
                    ? `${load.driverFirstName} ${load.driverLastName}`
                    : 'Not Checked In'}
                </td>

                <td>{load.truckingCompany || '-'}</td>
                <td>{load.trailerNumber || '-'}</td>
                <td>{load.phoneNumber || '-'}</td>
                <td>{formatStatus(load.status)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default App;