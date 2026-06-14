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
   * Dashboard load rows.
   */
  const [loads, setLoads] = useState([]);

  /*
   * Dock door visualization rows.
   */
  const [dockDoors, setDockDoors] = useState([]);

  /*
   * Driver check-in form data.
   *
   * The driver does NOT enter:
   * - database ID
   * - pickup date
   *
   * The backend automatically uses today's date.
   */
  const [formData, setFormData] = useState({
    loadNumber: '',
    driverFirstName: '',
    driverLastName: '',
    truckingCompany: '',
    phoneNumber: '',
    trailerNumber: ''
  });

  const [message, setMessage] = useState('');

  /*
   * CSV upload state.
   *
   * The shipper still picks a scheduled pickup date
   * when uploading the CSV.
   */
  const [uploadDate, setUploadDate] = useState('');
  const [uploadFile, setUploadFile] = useState(null);
  const [uploadMessage, setUploadMessage] = useState('');

  /*
   * Dashboard date filter.
   */
  const [dashboardDate, setDashboardDate] = useState('');

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

  function loadDockDoorData() {
    fetch('http://localhost:8080/api/dock-doors')
      .then(response => response.json())
      .then(data => setDockDoors(data))
      .catch(error => console.error('Error loading dock door data:', error));
  }

  useEffect(() => {
    loadDashboardData();
    loadDockDoorData();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value
    });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch('http://localhost:8080/api/checkins', {
      method: 'POST',

      headers: {
        'Content-Type': 'application/json'
      },

      /*
       * Sends only the load number and driver information.
       * The backend uses today's date automatically.
       */
      body: JSON.stringify(formData)
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(
            'Check-in failed. Please verify the load number or see the shipping office.'
          );
        }

        return response.json();
      })
      .then(data => {
        setMessage(
          `Check-in submitted for ${data.driverFirstName} ${data.driverLastName}`
        );

        setFormData({
          loadNumber: '',
          driverFirstName: '',
          driverLastName: '',
          truckingCompany: '',
          phoneNumber: '',
          trailerNumber: ''
        });

        /*
         * Refresh using the dashboard's current selected filter.
         */
        loadDashboardData();
        loadDockDoorData();
      })
      .catch(error => {
        console.error('Error submitting check-in:', error);
        setMessage(error.message);
      });
  }

  function handleFileChange(event) {
    setUploadFile(event.target.files[0]);
  }

  function handleUploadSubmit(event) {
    event.preventDefault();

    if (!uploadDate || !uploadFile) {
      setUploadMessage('Please select a pickup date and CSV file.');
      return;
    }

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
        setUploadMessage(data);
        setUploadFile(null);

        /*
         * After upload, show the uploaded pickup date on the dashboard.
         */
        setDashboardDate(uploadDate);
        loadDashboardData(uploadDate);
        setCurrentView('dashboard');
      })
      .catch(error => {
        console.error('Error uploading CSV:', error);
        setUploadMessage(error.message);
      });
  }

  function handleDashboardDateChange(event) {
    const selectedDate = event.target.value;

    setDashboardDate(selectedDate);
    loadDashboardData(selectedDate);
  }

  function clearDashboardDateFilter() {
    setDashboardDate('');
    loadDashboardData('');
  }

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

  function formatDateTime(value) {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString();
  }

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
                Load Number
                <input
                  type="text"
                  name="loadNumber"
                  value={formData.loadNumber}
                  onChange={handleChange}
                  required
                />
              </label>

              <p className="field-note">
                Enter the load number from your paperwork. Today&apos;s pickup date is used automatically.
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
                  <th>Door</th>
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
                    <td>{load.doorNumber || '-'}</td>

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