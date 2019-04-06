/*
 * Copyright (C) 2019 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.logics.financial.payment;

import br.com.webbudget.application.components.dto.PaymentWrapper;
import br.com.webbudget.domain.entities.financial.CreditCardInvoice;
import br.com.webbudget.domain.entities.financial.Payment;
import br.com.webbudget.domain.entities.financial.PeriodMovement;
import br.com.webbudget.domain.exceptions.BusinessLogicException;
import br.com.webbudget.domain.repositories.financial.CreditCardInvoiceRepository;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Simple validator to check the state of a {@link CreditCardInvoice} before we can save the {@link Payment} of any
 * {@link PeriodMovement}
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 3.0.0, 17/03/2019
 */
@Dependent
public class InvoiceStateValidator implements PaymentSavingLogic {

    @Inject
    private CreditCardInvoiceRepository creditCardInvoiceRepository;

    /**
     * {@inheritDoc}
     *
     * @param value
     */
    @Override
    public void run(PaymentWrapper value) {

        if (value.isCreditCardPayment()) {

            final CreditCardInvoice invoice = this.creditCardInvoiceRepository
                    .findByCardAndFinancialPeriod(value.getCreditCard(), value.getFinancialPeriod())
                    .orElseThrow(() -> new BusinessLogicException("error.credit-card-invoice.not-found"));

            if (invoice.isPaid() || invoice.isClosed()) {
                throw new BusinessLogicException("error.credit-card-invoice.invoice-closed-or-paid");
            }
        }
    }
}