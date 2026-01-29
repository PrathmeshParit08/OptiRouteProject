import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const RouteSearchPage = () => {
    const [from, setFrom] = useState('');
    const [to, setTo] = useState('');
    const [results, setResults] = useState(null);
    const navigate = useNavigate();

    const searchRoutes = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post(`/routes/suggest?from=${from}&to=${to}`);
            setResults(response.data);
        } catch (error) {
            alert('Error fetching routes');
        }
    };

    const handleSelect = async (routeId) => {
        try {
            const response = await api.post('/payment/initiate', { routeId });
            // Backend returns relative URL starting with /payment, navigate to it
            navigate(response.data.paymentUrl);
        } catch (error) {
            alert('Error initiating payment');
        }
    };

    return (
        <div className="search-page">
            <h2>Find Routes</h2>
            <form onSubmit={searchRoutes}>
                <input placeholder="From (e.g. New York)" value={from} onChange={e => setFrom(e.target.value)} />
                <input placeholder="To (e.g. London)" value={to} onChange={e => setTo(e.target.value)} />
                <button type="submit">Search</button>
            </form>

            {results && (
                <div className="results">
                    {results.bestRoute && (
                        <div className="best-route" style={{ border: '2px solid green', padding: '10px', margin: '10px 0' }}>
                            <h3>Best Route (Score: {results.bestRoute.efficiencyScore.toFixed(1)})</h3>
                            <p>Type: {results.bestRoute.transportType}</p>
                            <p>Duration: {results.bestRoute.durationMinutes} min</p>
                            <p>Cost: ${results.bestRoute.cost}</p>
                            <button onClick={() => handleSelect(results.bestRoute.routeId)}>Select</button>
                        </div>
                    )}

                    {results.otherRoutes && results.otherRoutes.length > 0 && (
                        <div className="other-routes">
                            <h3>Other Options</h3>
                            {results.otherRoutes.map(route => (
                                <div key={route.routeId} className="route-card" style={{ border: '1px solid gray', padding: '10px', margin: '5px 0' }}>
                                    <p>{route.transportType} | {route.durationMinutes} min | ${route.cost}</p>
                                    <button onClick={() => handleSelect(route.routeId)}>Select</button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default RouteSearchPage;
