# Building - Backend challenge

## Scope

In this challenge you should build an API for an application that stores and manages investments, it should have the following features:

1. **Creation** of an investment with an owner, a creation date and an amount. - Done

   1. The creation date of an investment can be today or a date in the past.
   2. An investment should not be or become negative.

2. **View** of an investment with its initial amount and expected balance. - Done

   1. Expected balance should be the sum of the invested amount and the [gains]. - Done
   2. If an investment was already withdrawn then the balance must reflect the gains of that investment.

3. **Withdrawal** of a investment.

   1. The withdraw will always be the sum of the initial amount and its gains,
      partial withdrawn is not supported.
   2. Withdrawals can happen in the past or today, but can't happen before the investment creation or the future.
   3. Taxes need to be applied to the withdrawals before showing the final value.

4. **List** of a person's investments - Done
   1. This list should have pagination.

### Gain Calculation

The investment will pay 0.52% every month in the same day of the investment creation.

Given that the gain is paid every month, it should be treated as compound gain, which means that every new period (month) the amount gained will become part of the investment balance for the next payment.

### Taxation

When money is withdrawn, tax is triggered. Taxes apply only to the profit/gain portion of the money withdrawn. For example, if the initial investment was 1000.00, the current balance is 1200.00, then the taxes will be applied to the 200.00.

The tax percentage changes according to the age of the investment:

- If it is less than one year old, the percentage will be 22.5% (tax = 45.00).
- If it is between one and two years old, the percentage will be 18.5% (tax = 37.00).
- If older than two years, the percentage will be 15% (tax = 30.00).

## Credits

Source: [Coderockr/back-end-test](https://github.com/Coderockr/backend-test/tree/main)
