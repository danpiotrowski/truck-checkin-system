import { useEffect, useState } from 'react';
import './App.css';

function App() {
  /*
   * currentView controls which page-like section is shown.
   *
   * dashboard = driver check-in and shipping dashboard
   * upload    = CSV upload page
   * doors     = dock door visualization page
   */
  const [currentView, setCurrentView] = useState('dashboard');

  /*
   * loads stores the dashboard rows that come from:
   *
   * GET /api/dashboard/loads
   */
  const [loads, setLoads] = useState([]);

  /*
   * dockDoors stores door visualization rows that come from:
   *
   * GET /api/dock-doors
   */
  const [dockDoors, setDockDoors] = useState([]);

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
   */
  const [dashboardDate, setDashboardDate] = useState('');

  /*
   * Loads dashboard data from Spring Boot.
   *
   * If selectedDate has a value, the backend filters by pickup date.
   * If selectedDate is blank, the backend returns all active loads.
   */
  function loadDashboardData(selectedDate = dashboardDate) {
    let url = 'http://localhost:8080/api/dashboard/loads';

    if (selectedDate) {
      url = `${url}?date=${selectedDate}`;
    }

    fetch(url)
      .then(response => response.json())
      .then(data => setLoads(data))
      .catch(error => console.error('Error loading dashboard data:', error));
  }

  /*
   * Loads dock door visualization data from Spring Boot.
   */
  function loadDockDoorData() {
    fetch('http://localhost:8080/api/dock-doors')
      .then(response => response.json())
      .then(data => setDockDoors(data))
      .catch(error => console.error('Error loading dock door data:', error));
  }

  /*
   * Runs once when the React page first loads.
   */
  useEffect(() => {
    loadDashboardData();
    loadDockDoorData();
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
         * Refresh dashboard data because driver check-in
         * changes the load status to WAITING.
         */
        loadDashboardData();

        /*
         * Refresh door data too so both views stay current.
         */
        loadDockDoorData();
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
    event.preventDefault();

    if (!uploadDate || !uploadFile) {
      setUploadMessage('Please select a pickup date and CSV file.');
      return;
    }

    /*
     * FormData is required for sending files to Spring Boot.
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
         * Spring Boot returns plain text:
         *
         * Upload complete. Loads created: X, items created: Y
         */
        return response.text();
      })
      .then(data => {
        setUploadMessage(data);
        setUploadFile(null);

        /*
         * After upload, filter the dashboard to the date
         * that was just uploaded.
         */
        setDashboardDate(uploadDate);
        loadDashboardData(uploadDate);

        /*
         * Send the shipper back to the dashboard after upload.
         */
        setCurrentView('dashboard');
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
   * Converts internal load/door status values into user-friendly text.
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
      case 'AVAILABLE':
        return 'Available';
      case 'OCCUPIED':
        return 'Occupied';
      case 'DOWN':
        return 'Down';
      default:
        return status || '-';
    }
  }

  /*
   * Formats timestamps from Spring Boot.
   *
   * If no timestamp exists, show a dash.
   */
  function formatDateTime(value) {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString();
  }

  /*
   * Returns the correct timestamp label for each dock door status.
   */
  function getDoorTimestampLabel(door) {
    if (door.status === 'AVAILABLE') {
      return `Available since: ${formatDateTime(door.availableSince)}`;
    }

    if (door.status === 'OCCUPIED') {
      return `Occupied since: ${formatDateTime(door.occupiedSince)}`;
    }

    if (door.status === 'DOWN') {
      return `Down since: ${formatDateTime(door.downSince)}`;
    }

    return `Last changed: ${formatDateTime(door.lastStatusChangedAt)}`;
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Truck Check-In System</h1>

      <nav className="app-nav">
        <button
          type="button"
          className={currentView === 'dashboard' ? 'nav-button active' : 'nav-button'}
          onClick={() => {
            setCurrentView('dashboard');
            loadDashboardData();
          }}
        >
          Shipping Dashboard
        </button>

        <button
          type="button"
          className={currentView === 'upload' ? 'nav-button active' : 'nav-button'}
          onClick={() => setCurrentView('upload')}
        >
          CSV Upload
        </button>

        <button
          type="button"
          className={currentView === 'doors' ? 'nav-button active' : 'nav-button'}
          onClick={() => {
            setCurrentView('doors');
            loadDockDoorData();
          }}
        >
          Door Visualization
        </button>
      </nav>

      {currentView === 'dashboard' && (
        <>
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
        </>
      )}

      {currentView === 'upload' && (
        <section className="form-card">
          <h2>CSV Load Upload</h2>

          <p className="field-note">
            Upload the daily outbound CSV and select the pickup date
            that should be assigned to those loads.
          </p>

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
      )}

      {currentView === 'doors' && (
        <section>
          <div className="section-header">
            <h2>Door Visualization</h2>

            <button type="button" onClick={loadDockDoorData}>
              Refresh Doors
            </button>
          </div>

          <div className="door-legend">
            <span className="legend-item available">Green = Available</span>
            <span className="legend-item occupied">Yellow = Occupied</span>
            <span className="legend-item down">Red = Down</span>
          </div>

          <div className="door-grid">
            {dockDoors.map(door => (
              <div
                key={door.doorId}
                className={`door-card ${door.status.toLowerCase()}`}
              >
                <div className="door-card-header">
                  <h3>Door {door.doorNumber}</h3>
                  <span className="door-status">{formatStatus(door.status)}</span>
                </div>

                <p className="door-time">
                  {getDoorTimestampLabel(door)}
                </p>

                {door.status === 'OCCUPIED' && (
                  <div className="door-load-info">
                    <p><strong>Load:</strong> {door.loadNumber || '-'}</p>

                    <p>
                      <strong>Driver:</strong>{' '}
                      {door.driverFirstName
                        ? `${door.driverFirstName} ${door.driverLastName}`
                        : '-'}
                    </p>

                    <p><strong>Company:</strong> {door.truckingCompany || '-'}</p>
                    <p><strong>Trailer:</strong> {door.trailerNumber || '-'}</p>
                  </div>
                )}

                {door.status === 'DOWN' && (
                  <div className="door-down-info">
                    <p>
                      <strong>Reason:</strong>{' '}
                      {door.downReason || 'No reason entered'}
                    </p>
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}

export default App;