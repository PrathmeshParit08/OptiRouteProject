import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optiroute.dto.PaymentRedirectResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.model.Payment;
import com.optiroute.model.PaymentStatus;
import com.optiroute.repository.DirectRouteRepository;
import com.optiroute.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private DirectRouteRepository directRouteRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentRedirectResponse initiatePayment(Long routeId) {

        DirectRoute route = directRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        Payment payment = new Payment();
        payment.setRouteId(routeId);
        payment.setAmount(route.getCost());
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        return PaymentRedirectResponse.builder()
                .paymentUrl("/payment?paymentId=" + payment.getId())
                .routeId(routeId)
                .amount(route.getCost())
                .build();
    }

    public Payment confirmPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // MOCK confirmation
        payment.setStatus(PaymentStatus.SUCCESS);

        return paymentRepository.save(payment);
    }
}
