package dev.edney.picpay.exception;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.math.BigDecimal;

public class InsufficientBalanceException extends PicPayException {
    @Override
    public ProblemDetail toProblemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setTitle("Insufficient balance");
        pb.setDetail("You cannot transfer a value bigger than your current balance.");

        return pb;
    }
}
