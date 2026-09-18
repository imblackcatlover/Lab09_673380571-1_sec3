package com.example.lab9.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.lab9.model.Account;
import com.example.lab9.model.DepositTransaction;
import com.example.lab9.repository.AccountRepository;
import com.example.lab9.repository.DepositRepository;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final DepositRepository depositRepository;

    public DepositService(AccountRepository accountRepository,
                          DepositRepository depositRepository) {
        this.accountRepository = accountRepository;
        this.depositRepository = depositRepository;
    }

    @Transactional
    public void deposit(Long accountId, Double amount) {

        if (amount == null || !Double.isFinite(amount) || amount <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount must be a finite positive number"
            );
        }

        // 1. ค้นหาบัญชี
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        // 2. เพิ่มยอดเงินและบันทึกบัญชี
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        // 3. บันทึกประวัติการฝากเงิน
        DepositTransaction transaction = new DepositTransaction();
        transaction.setAmount(amount);
        transaction.setAccount(account);

        depositRepository.save(transaction);

        // เปิดบรรทัดนี้เมื่อต้องการทดลอง Rollback
        //throw new RuntimeException("Test Rollback");
    }
}