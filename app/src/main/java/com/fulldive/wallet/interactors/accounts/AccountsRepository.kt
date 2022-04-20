package com.fulldive.wallet.interactors.accounts

import com.fulldive.wallet.di.modules.DefaultRepositoryModule
import com.fulldive.wallet.models.BaseChain
import com.joom.lightsaber.ProvidedBy
import io.reactivex.Completable
import io.reactivex.Single
import wannabit.io.cosmostaion.dao.Account
import javax.inject.Inject

@ProvidedBy(DefaultRepositoryModule::class)
class AccountsRepository @Inject constructor(
    private val accountsLocalStorage: AccountsLocalStorage
) {

    fun getAccounts(): Single<List<Account>> {
        return accountsLocalStorage.getAccounts()
    }

    fun getAccount(accountId: Long): Single<Account> {
        return accountsLocalStorage.getAccount(accountId)
    }

    fun getAccount(chain: BaseChain, address: String): Single<Account> {
        return accountsLocalStorage.getAccount(chain, address)
    }

    fun getSelectedAccount(): Single<Account> {
        return accountsLocalStorage.getSelectedAccount()
    }

    fun getCurrentAccount(): Account {
        return accountsLocalStorage.getCurrentAccount()
    }

    fun getAccountsByAddress(address: String): Single<List<Account>> {
        return accountsLocalStorage.getAccountsByAddress(address)
    }

    fun getAccountsByChain(chain: BaseChain): List<Account> {
        return accountsLocalStorage.getAccountsByChain(chain)
    }

    fun getSortedChains(): Single<List<BaseChain>> {
        return accountsLocalStorage.getSortedChains()
    }

    fun selectAccount(id: Long): Completable {
        return accountsLocalStorage.selectAccount(id)
    }

    fun addAccount(account: Account): Single<Long> {
        return accountsLocalStorage.addAccount(account)
    }

    fun updateAccount(account: Account): Completable {
        return accountsLocalStorage.updateAccount(account)
    }

    fun deleteAccount(accountId: Long): Completable {
        return accountsLocalStorage.deleteAccount(accountId)
    }

    fun checkExistsPassword(): Single<Boolean> {
        return accountsLocalStorage.checkExistsPassword()
    }

    fun setHiddenChains(items: List<BaseChain>): Completable {
        return accountsLocalStorage.setHiddenChains(items)
    }

    fun getHiddenChains(): Single<List<BaseChain>> {
        return accountsLocalStorage.getHiddenChains()
    }
}