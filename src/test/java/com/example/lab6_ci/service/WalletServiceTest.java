package com.example.lab6_ci.service;

import com.example.lab6_ci.dto.WalletResponse;
import com.example.lab6_ci.model.Wallet;
import com.example.lab6_ci.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.AssertionErrors;

import java.util.Optional;

public class WalletServiceTest {
    private WalletService walletService;
    private RiskClient riskClient;
    private WalletRepository walletRepository;

    //Arrange comun de todas las pruebas
    @BeforeEach
    public void setUp(){
        walletRepository = Mockito.mock(WalletRepository.class);
        riskClient = Mockito.mock(RiskClient.class);
        walletService = new WalletService(walletRepository, riskClient);
    }

    @Test
    void createWallet_ValidaData_shouldSaveAndReturnResponse(){
        //Arrange
        String email = "francisco.jimenez@ejemplo.com";
        double balance = 150.00;

        Mockito.when(riskClient.isBlocked(email)).thenReturn(false);

        Mockito.when(walletRepository.existsByOwnerEmail(email)).thenReturn(false);

        Mockito.when(walletRepository.save(ArgumentMatchers.any(Wallet.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        //Act
        WalletResponse response = walletService.createWallet(email, balance);

        //Assert
        AssertionErrors.assertNotNull("El Id del Wallet no debe ser NULL", response.getWalletId());
        Assertions.assertEquals(150.00, response.getBalance(), 0.0001);

        Mockito.verify(riskClient).isBlocked(email);
        Mockito.verify(walletRepository).existsByOwnerEmail(email);
        Mockito.verify(walletRepository).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void creteWallet_invalidData_shouldThrowException_andNotCallDependencies(){
        //Arrange
        String invalid = "francisco.jimenez-ejemplo.com";
        double balance = 150.00;

        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class,()
                -> walletService.createWallet(invalid, balance));

        //No debe llamar a ninguna dependencia porque falla la validacion
        Mockito.verifyNoInteractions(walletRepository, riskClient);
    }

    @Test
    void deposit_walletNotFound_shouldThrowException(){
        //Arrange
        String walletId = "No-exite";

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        //Act + Assert
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, ()
                -> walletService.deposit(walletId, 50));

        Assertions.assertEquals("Wallet not found", ex.getMessage());
        Mockito.verify(walletRepository).findById(walletId);
        Mockito.verify(walletRepository, Mockito.never()).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void deposit_shouldUpdateBalance_andSave_usingCaptor(){
        //Arrange
        Wallet wallet = new Wallet("francisco.jimenez@ejemplo.com", 100.00);
        String walletId = wallet.getId();

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        //Act
        double newBalance = walletService.deposit(walletId, 30.00);

        Mockito.verify(walletRepository).save(captor.capture());
        Wallet saved = captor.getValue();
        Assertions.assertEquals(newBalance, saved.getBalance(), 0.0001);

    }
}
