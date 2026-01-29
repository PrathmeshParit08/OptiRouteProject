import { useSearchParams, useNavigate } from 'react-router-dom';

const PaymentPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const routeId = searchParams.get('routeId');
    const amount = searchParams.get('amount');

    const handlePay = () => {
        alert('Payment Successful! Ticket Booked.');
        navigate('/search');
    };

    return (
        <div className="payment-page">
            <h2>Confirm Payment</h2>
            <p>Route ID: {routeId}</p>
            <p>Total Amount: <strong>${amount}</strong></p>
            <button onClick={handlePay} style={{ backgroundColor: 'green', color: 'white', padding: '10px' }}>Pay Now</button>
        </div>
    );
};

export default PaymentPage;
