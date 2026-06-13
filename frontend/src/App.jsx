import { useEffect, useState } from 'react';
import './App.css';

function App() {
  /*
   * loads stores the load records that come from PostgreSQL
   * through the Spring Boot GET /api/loads endpoint.
   */
  const [loads, setLoads] = useState([]);

  /*
   * formData stores everything the driver types into the check-in form.
   *
   * Each property name should match the Java field name in DriverCheckin.java.
   * That makes it easy for Spring Boot to convert the JSON into a Java object.
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
   * message stores feedback for the user after they submit the form.
   * Example: "Check-in submitted for John Smith"
   */
  const [message, setMessage] = useState('');

  /*
   * useEffect runs when the page first loads.
   *
   * We use it here to load the shipping dashboard data from Spring Boot.
   */
  useEffect(() => {
    fetch('http://localhost:8080/api/dashboard/loads')
      .then(response => response.json())
      .then(data => setLoads(data))
      .catch(error => console.error('Error loading loads:', error));
  }, []);

  /*
   * This function runs every time the driver types in an input box.
   *
   * event.target.name tells us which field changed.
   * event.target.value gives us the new value.
   */
  function handleChange(event) {
    const { name, value } = event.target;

    /*
     * Keep the old form data, but replace only the field that changed.
     *
     * Example:
     * If name is "driverFirstName", this updates only driverFirstName.
     */
    setFormData({
      ...formData,
      [name]: value
    });
  }

  /*
   * This function runs when the driver clicks the Check In button.
   */
  function handleSubmit(event) {
    /*
     * Prevents the browser from refreshing the page.
     * HTML forms refresh by default, but React apps usually handle forms with JavaScript.
     */
    event.preventDefault();

    /*
     * Send the driver check-in to Spring Boot.
     *
     * POST means we are creating a new driver_checkins row in PostgreSQL.
     */
    fetch('http://localhost:8080/api/checkins', {
      method: 'POST',

      /*
       * Tell Spring Boot that the request body is JSON.
       */
      headers: {
        'Content-Type': 'application/json'
      },

      /*
       * Convert the JavaScript object into JSON text.
       *
       * loadId starts as text because all HTML input values are strings.
       * Number(formData.loadId) converts it into a number before sending it.
       */
      body: JSON.stringify({
        ...formData,
        loadId: Number(formData.loadId)
      })
    })
	.then(response => {

		/*
	     * response.ok is true for successful responses
         * (HTTP 200–299).
         *
         * If it's false, the backend rejected the request.
         */
	if (!response.ok) {

		/*
		 * Throw a friendly error that the catch block
		 * can display to the driver.
		 */
    throw new Error(
      'This load has already been checked in. Please see the shipping office.'
    );
  }

  return response.json();
})

.then(data => {

  /*
   * Show a success message using the data returned
   * by Spring Boot.
   */
  setMessage(
    `Check-in submitted for ${data.driverFirstName} ${data.driverLastName}`
  );

  /*
   * Clear the form after a successful check-in.
   */
  setFormData({
    loadId: '',
    driverFirstName: '',
    driverLastName: '',
    truckingCompany: '',
    phoneNumber: '',
    trailerNumber: ''
  });
})

.catch(error => {

  /*
   * Log the technical details to the browser console.
   */
  console.error('Error submitting check-in:', error);

  /*
   * Display the friendly error message.
   */
  setMessage(error.message);
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
			Temporary: use 1, 2, or 3 from the dashboard table.
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