package dev.edney.picpay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class NotAuthorizedException extends PicPayException {
    @Override
    public ProblemDetail toProblemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setTitle("Transfer not authorized by authorization service.");

        return pb;
    }
}
