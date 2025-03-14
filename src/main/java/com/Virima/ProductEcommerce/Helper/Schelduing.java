package com.Virima.ProductEcommerce.Helper;

import com.Virima.ProductEcommerce.Entity.OrderAudit;
import com.Virima.ProductEcommerce.Entity.Orders;
import com.Virima.ProductEcommerce.Entity.PromoCode;
import com.Virima.ProductEcommerce.Repo.OrderAudictRepository;
import com.Virima.ProductEcommerce.Repo.OrderRepository;
import com.Virima.ProductEcommerce.Repo.PromocodeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.Virima.ProductEcommerce.Entity.PromoCodeStatus.ACTIVE;
import static com.Virima.ProductEcommerce.Entity.PromoCodeStatus.INACTIVE;

@Component
public class Schelduing {

    @Autowired
    PromocodeRepo promocodeRepo;

    @Autowired
    OrderAudictRepository orderAudictRepository;

    @Autowired
    OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deactivateExpiredPromoCodes() {
        List<PromoCode> promoCodes = promocodeRepo.findAll();
        Date currentDate = new Date();
        for (PromoCode promo : promoCodes) {
            if (promo.getEndDate().before(currentDate) && promo.getStatus().equals(ACTIVE)) {
                promo.setStatus(INACTIVE);
                promocodeRepo.save(promo);
                System.out.println("Promo code " + promo.getCode() + " has expired and is now inactive.");
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void updateOrderStatusToDispatched() {
        // Fetch all orders with status "paid"
        List<Orders> paidOrders = orderRepository.findByOrderStatus("paid");

        // For each paid order, update the status to "dispatched" and create an audit record
        for (Orders order : paidOrders) {
            String previousStatus = order.getOrderStatus();
            String newStatus = "dispatched";

            // Set the new status to "dispatched"
            order.setOrderStatus(newStatus);
            orderRepository.save(order); // Save the updated order

            // Create an order audit record
            OrderAudit orderAudit = new OrderAudit();
            orderAudit.setOrderId(order.getId());
            orderAudit.setPreviousStatus(previousStatus);
            orderAudit.setNewStatus(newStatus);

            // Save the audit record
            orderAudictRepository.save(orderAudit);
        }
    }


    @Scheduled(cron = "0 0 0 */7 * ?")
    @Transactional
    public void updateOrderStatusToDillary() {
        // Fetch all orders with status "paid"
        List<Orders> paidOrders = orderRepository.findByOrderStatus("paid");

        // For each paid order, update the status to "dispatched" and create an audit record
        for (Orders order : paidOrders) {
            String previousStatus = order.getOrderStatus();
            String newStatus = "completed";

            // Set the new status to "dispatched"
            order.setOrderStatus(newStatus);
            orderRepository.save(order); // Save the updated order

            // Create an order audit record
            OrderAudit orderAudit = new OrderAudit();
            orderAudit.setOrderId(order.getId());
            orderAudit.setPreviousStatus(previousStatus);
            orderAudit.setNewStatus(newStatus);

            // Save the audit record
            orderAudictRepository.save(orderAudit);
        }
    }

//    @Scheduled(cron = "0/10 * * * * ?")
//    public void sayHi() {
//        System.out.println("Hi");
//    }
}
