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
    const [cardDetails, setCardDetails] = useState({
        number: '',
        expiry: '',
        cvc: '',
        name: ''
    });
    const { showPopup } = usePopup();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
        }
    }, [navigate]);

    const validatePayment = () => {
        const { name, number, expiry, cvc } = cardDetails;

        // Name validation
        if (!name.trim()) {
            showPopup('Cardholder name is required', 'warning');
            return false;
        }

        // Card number validation (16 digits)
        const cleanNumber = number.replace(/\s/g, '');
        if (!/^\d{16}$/.test(cleanNumber)) {
            showPopup('Card number must be exactly 16 digits', 'warning');
            return false;
        }

        // Expiry validation (MM/YY and future date)
        if (!/^\d{2}\/\d{2}$/.test(expiry)) {
            showPopup('Expiry must be in MM/YY format', 'warning');
            return false;
        }

        const [month, year] = expiry.split('/').map(num => parseInt(num, 10));
        const currentYear = new Date().getFullYear() % 100; // Last 2 digits
        const currentMonth = new Date().getMonth() + 1;

        if (month < 1 || month > 12) {
            showPopup('Invalid month', 'warning');
            return false;
        }

        if (year < currentYear || (year === currentYear && month < currentMonth)) {
            showPopup('Card has expired', 'warning');
            return false;
        }

        // CVC validation
        if (!/^\d{3}$/.test(cvc)) {
            showPopup('CVC must be 3 digits', 'warning');
            return false;
        }

        return true;
    };

    const handlePay = async (e) => {
    e.preventDefault();

    // Step 1: Validate payment details
    if (!validatePayment()) return;

    setLoading(true);

    // Step 2: Simulate payment processing delay
    setTimeout(async () => {
        try {
            // Step 3: (Optional) Call backend payment initiation
            if (routeId) {
                await api.post('/payment/initiate', {
                    routeId: parseInt(routeId)
                });
            }

            // Step 4: Create booking only after payment success
            if (routeId && date) {
                await api.post('/bookings/create', {
                    routeId: parseInt(routeId),
                    journeyDate: date,
                    fromCity: fromCity,
                    toCity: toCity
                });

                showPopup('Payment Successful! Ticket Booked.', 'success');
                navigate('/bookings');
            } else {
                // Fallback for demo mode
                showPopup('Payment Successful (Simulation Mode).', 'success');
                navigate('/search');
            }
        } catch (error) {
            console.error('Payment/Booking error:', error);
            showPopup(
                'Payment successful but booking failed',
                'error'
            );
        } finally {
            setLoading(false);
        }
    }, 2000); // 2-second simulated gateway delay
};


    const handleChange = (e) => {
        const { name, value } = e.target;
        setCardDetails(prev => ({ ...prev, [name]: value }));
    };

    return (
        <div style={{
            background: 'linear-gradient(160deg, rgba(30, 41, 59, 0.6) 0%, rgba(15, 23, 42, 0.8) 100%)',
            backdropFilter: 'blur(25px)',
            WebkitBackdropFilter: 'blur(25px)',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            borderRadius: '24px',
            padding: '3rem',
            boxShadow: '0 20px 40px rgba(0, 0, 0, 0.4)',
            maxWidth: '500px',
            width: '100%',
            margin: '0 auto',
            animation: 'fadeInUp 0.8s ease-out'
        }}>
            <h2 style={{
                textAlign: 'center',
                marginBottom: '2rem',
                fontSize: '2rem',
                background: 'linear-gradient(to right, #818cf8, #c084fc, #f472b6)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                fontWeight: '800'
            }}>Secure Checkout</h2>

            <div style={{
                background: 'rgba(30, 41, 59, 0.5)',
                borderRadius: '16px',
                padding: '1.5rem',
                marginBottom: '2rem',
                border: '1px solid rgba(255, 255, 255, 0.05)'
            }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', color: '#94a3b8' }}>
                    <span>Route ID</span>
                    <span style={{ color: '#f1f5f9', fontWeight: '600' }}>#{routeId}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ color: '#94a3b8' }}>Total Amount</span>
                    <span style={{ fontSize: '1.5rem', fontWeight: '800', color: '#34d399' }}>₹{amount}</span>
                </div>
            </div>

            <form onSubmit={handlePay}>
                <div className="form-group">
                    <label style={{ display: 'block', marginBottom: '0.5rem', color: '#94a3b8' }}>Cardholder Name</label>
                    <input
                        className="form-input"
                        type="text"
                        name="name"
                        placeholder="John Doe"
                        value={cardDetails.name}
                        onChange={handleChange}
                        required
                        style={{ width: '100%', padding: '0.9rem', borderRadius: '12px', background: 'rgba(15, 23, 42, 0.6)', border: '1px solid rgba(255, 255, 255, 0.1)', color: 'white' }}
                    />
                </div>

                <div className="form-group">
                    <label style={{ display: 'block', marginBottom: '0.5rem', color: '#94a3b8' }}>Card Number</label>
                    <input
                        className="form-input"
                        type="text"
                        name="number"
                        placeholder="0000 0000 0000 0000"
                        maxLength="19"
                        value={cardDetails.number}
                        onChange={handleChange}
                        required
                        style={{ width: '100%', padding: '0.9rem', borderRadius: '12px', background: 'rgba(15, 23, 42, 0.6)', border: '1px solid rgba(255, 255, 255, 0.1)', color: 'white' }}
                    />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                    <div className="form-group">
                        <label style={{ display: 'block', marginBottom: '0.5rem', color: '#94a3b8' }}>Expiry</label>
                        <input
                            className="form-input"
                            type="text"
                            name="expiry"
                            placeholder="MM/YY"
                            maxLength="5"
                            value={cardDetails.expiry}
                            onChange={handleChange}
                            required
                            style={{ width: '100%', padding: '0.9rem', borderRadius: '12px', background: 'rgba(15, 23, 42, 0.6)', border: '1px solid rgba(255, 255, 255, 0.1)', color: 'white' }}
                        />
                    </div>
                    <div className="form-group">
                        <label style={{ display: 'block', marginBottom: '0.5rem', color: '#94a3b8' }}>CVC</label>
                        <input
                            className="form-input"
                            type="text"
                            name="cvc"
                            placeholder="123"
                            maxLength="3"
                            value={cardDetails.cvc}
                            onChange={handleChange}
                            required
                            style={{ width: '100%', padding: '0.9rem', borderRadius: '12px', background: 'rgba(15, 23, 42, 0.6)', border: '1px solid rgba(255, 255, 255, 0.1)', color: 'white' }}
                        />
                    </div>
                </div>

                <button
                    type="submit"
                    className="btn-primary"
                    disabled={loading}
                    style={{
                        width: '100%',
                        marginTop: '1.5rem',
                        padding: '1rem',
                        background: loading ? '#4b5563' : 'linear-gradient(135deg, #059669, #10b981)',
                        border: 'none',
                        borderRadius: '12px',
                        color: 'white',
                        fontWeight: '600',
                        fontSize: '1.1rem',
                        cursor: loading ? 'not-allowed' : 'pointer',
                        transition: 'all 0.3s ease',
                        boxShadow: '0 4px 6px rgba(0,0,0,0.2)'
                    }}
                >
                    {loading ? 'Processing...' : 'Pay Now'}
                </button>
            </form>
        </div>
    );
};

export default PaymentPage;
