package dev.edney.picpay.service;

import dev.edney.picpay.dto.TransferDto;
import dev.edney.picpay.exception.InsufficientBalanceException;
import dev.edney.picpay.entity.Transfer;
import dev.edney.picpay.entity.Wallet;
import dev.edney.picpay.exception.NotAuthorizedException;
import dev.edney.picpay.exception.TransferNotAllowedForWalletTypeException;
import dev.edney.picpay.exception.WalletNotFoundException;
import dev.edney.picpay.repository.TransferRepository;
import dev.edney.picpay.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TransferService {
    private final NotificationService notificationService;
    private final AuthorizationService authorizationService;
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    public TransferService(NotificationService notificationService,
                           AuthorizationService authorizationService,
                           TransferRepository transferRepository, WalletRepository walletRepository) {
        this.notificationService = notificationService;
        this.authorizationService = authorizationService;
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Transfer transfer(TransferDto transferDto) {
        var sender = walletRepository.findById(transferDto.payer())
                .orElseThrow(() -> new WalletNotFoundException(transferDto.payer()));

        var receiver = walletRepository.findById(transferDto.payee())
                .orElseThrow(() -> new WalletNotFoundException(transferDto.payee()));

        validateTransfer(transferDto, sender);

        sender.debit(transferDto.value());
        receiver.credit(transferDto.value());

        var transfer = new Transfer(sender, receiver, transferDto.value());

        walletRepository.save(receiver);
        walletRepository.save(sender);
        var transferResult = transferRepository.save(transfer);

        CompletableFuture.runAsync(() -> notificationService.sendNotification(transferResult));

        return transferResult;
    }

    private void validateTransfer(TransferDto transferDto, Wallet sender) {
        if (!sender.isTransferAllowedForWalletType()) {
            throw new TransferNotAllowedForWalletTypeException();
        }

        if (!sender.isBalanceEqualsOrGreaterThan(transferDto.value())) {
            throw new InsufficientBalanceException();
        }

        if (!authorizationService.isAuthorized(transferDto)) {
            throw new NotAuthorizedException();
        }
    }
}
