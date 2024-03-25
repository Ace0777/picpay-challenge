package br.com.challenge.transaction;

import br.com.challenge.authorization.AuthorizerService;
import br.com.challenge.notification.NotificationService;
import br.com.challenge.wallet.Wallet;
import br.com.challenge.wallet.WalletRepository;
import br.com.challenge.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;

    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository,WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService){
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction create(Transaction transaction){
        // 1- validar regra negocio

        validate(transaction);

        // 2- criar

        var newTransaction = transactionRepository.save(transaction);

        // 3 debitar carteira

        var wallet = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(wallet.debit(transaction.value()));

        //4 chamar serviços externos
        //autorização
        authorizerService.authorize(transaction);


        //notificação
        notificationService.notify(transaction);

        return newTransaction;
    }

    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
        .map(payee -> walletRepository.findById(transaction.payer())
         .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)//Usando SOLID melhorando a legebilidade do codigo...
                .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - " + transaction)))
                .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - " + transaction));
    }

    private static boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee());
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
}
