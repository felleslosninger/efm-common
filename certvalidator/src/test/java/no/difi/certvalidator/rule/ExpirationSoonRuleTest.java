package no.difi.certvalidator.rule;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.testutil.X509TestGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

public class ExpirationSoonRuleTest extends X509TestGenerator {

    @Test
    public void simple() throws Exception {
        int millis = 5 * 24 * 60 * 60 * 1000;
        Validator validatorHelper = new Validator(new ExpirationSoonRule(millis));

        Assert.assertTrue(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10))));
        Assert.assertTrue(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(6))));
        Assert.assertTrue(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5).plusMinutes(1))));
        Assert.assertFalse(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5))));
        Assert.assertFalse(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5).minusMinutes(1))));
        Assert.assertFalse(validatorHelper.isValid(createX509Certificate(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(4))));
    }

}
