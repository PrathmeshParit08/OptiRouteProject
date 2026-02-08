import { useState, useEffect } from 'react';
import { useLocation, useSearchParams, useNavigate } from 'react-router-dom';
import { usePopup } from '../context/PopupContext';
import api from '../services/api';

const PaymentPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const location = useLocation();

    const routeId = location.state?.routeId || searchParams.get('routeId');
    const amount = location.state?.amount || searchParams.get('amount');
    const date = location.state?.date;
    const fromCity = location.state?.from;
    const toCity = location.state?.to;

    const [loading, setLoading] = useState(false);
    const [paymentId, setPaymentId] = useState(null);

    const [cardDetails, setCardDetails] = useState({
        number: '',
        expiry: '',
        cvc: '',
        name: ''
    });

    const { showPopup } = usePopup();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) navigate('/login');
    }, [navigate]);

    const validatePayment = () => {
        const { name, number, expiry, cvc } = cardDetails;

        if (!name.trim()) {
            showPopup('Cardholder name is required', 'warning');
            return false;
        }

        const cleanNumber = number.replace(/\s/g, '');
        if (!/^\d{16}$/.test(cleanNumber)) {
            showPopup('Card number must be 16 digits', 'warning');
            return false;
        }

        if (!/^\d{2}\/\d{2}$/.test(expiry)) {
            showPopup('Expiry must be MM/YY', 'warning');
            return false;
        }

        const [month, year] = expiry.split('/').map(Number);
        const now = new Date();
        const currentYear = now.getFullYear() % 100;
        const currentMonth = now.getMonth() + 1;

        if (month < 1 || month > 12 || year < currentYear ||
            (year === currentYear && month < currentMonth)) {
            showPopup('Card has expired', 'warning');
            return false;
        }

        if (!/^\d{3}$/.test(cvc)) {
            showPopup('CVC must be 3 digits', 'warning');
            return false;
        }

        return true;
    };

    const handlePay = async (e) => {
        e.preventDefault();

        if (!validatePayment()) return;
        setLoading(true);

        try {
           
            const initiateRes = await api.post('/payment/initiate', {
                routeId: parseInt(routeId)
            });

            const pid = initiateRes.data.paymentId;
            setPaymentId(pid);

         
            await new Promise(resolve => setTimeout(resolve, 2000));

          
            await api.post(`/payment/confirm/${pid}`);

            
            await api.post('/bookings/create', {
                routeId: parseInt(routeId),
                journeyDate: date,
                fromCity,
                toCity
            });

            showPopup('Payment Successful! Ticket Booked.', 'success');
            navigate('/bookings');

        } catch (error) {
            console.error(error);

           
            if (paymentId) {
                await api.post(`/payment/fail/${paymentId}`);
            }

            showPopup('Payment failed. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCardDetails(prev => ({ ...prev, [name]: value }));
    };

    return (
        <div style={{
            background: 'linear-gradient(160deg, rgba(30,41,59,.6), rgba(15,23,42,.8))',
            backdropFilter: 'blur(25px)',
            borderRadius: '24px',
            padding: '3rem',
            maxWidth: '500px',
            margin: '0 auto',
            boxShadow: '0 20px 40px rgba(0,0,0,.4)'
        }}>
            <h2 style={{ textAlign: 'center', marginBottom: '2rem' }}>
                Secure Checkout
            </h2>

            <div style={{ marginBottom: '1.5rem' }}>
                <p>Route ID: #{routeId}</p>
                <h3>Amount: ₹{amount}</h3>
            </div>

            <form onSubmit={handlePay}>
                <input
                    type="text"
                    name="name"
                    placeholder="Cardholder Name"
                    value={cardDetails.name}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="number"
                    placeholder="Card Number"
                    maxLength="19"
                    value={cardDetails.number}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="expiry"
                    placeholder="MM/YY"
                    maxLength="5"
                    value={cardDetails.expiry}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="cvc"
                    placeholder="CVC"
                    maxLength="3"
                    value={cardDetails.cvc}
                    onChange={handleChange}
                    required
                />

                <button type="submit" disabled={loading}>
                    {loading ? 'Processing...' : 'Pay Now'}
                </button>
            </form>
        </div>
    );
};

export default PaymentPage;
