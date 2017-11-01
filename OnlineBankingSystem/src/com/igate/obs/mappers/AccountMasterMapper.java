package com.igate.obs.mappers;

import com.igate.obs.bean.AccountMaster;
import com.igate.obs.exception.BankingException;

public interface AccountMasterMapper {
	 public AccountMaster fetchAccountDetails(long accountId) throws BankingException;
}
