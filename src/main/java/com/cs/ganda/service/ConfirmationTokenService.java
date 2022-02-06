package com.cs.ganda.service;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Profile;

public interface ConfirmationTokenService {
    void sendActivationCode(ActivationData activationData);

    void sendActivationCode(Profile profile);

    String activatePhone(ActivationData activationData);

    ConfirmationToken activate(String itemId, String phone, String phoneIndex, String token);

    ConfirmationToken activateByToken(String token);

    ConfirmationToken getConfirmationToken(String token);
}
