import { useEffect, useState } from 'react';
import './App.css';

function App() {
  const [loads, setLoads] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/loads')
      .then(response => response.json())
      .then(data => setLoads(data))
      .catch(error => console.error('Error loading data:', error));
  }, []);

return (
  <div className="dashboard">
    <h1 className="dashboard-title">Truck Check-In Dashboard</h1>

    <table className="loads-table">
      <thead>
        <tr>
          <th>Load Number</th>
          <th>Carrier</th>
          <th>Trailer</th>
          <th>Status</th>
        </tr>
      </thead>

      <tbody>
        {loads.map(load => (
          <tr key={load.loadNumber}>
            <td>{load.loadNumber}</td>
            <td>{load.truckingCompany}</td>
            <td>{load.trailerNumber}</td>
            <td>{load.status}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
}

export default App;