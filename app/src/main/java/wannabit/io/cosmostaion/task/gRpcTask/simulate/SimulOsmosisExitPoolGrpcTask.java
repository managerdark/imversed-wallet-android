package wannabit.io.cosmostaion.task.gRpcTask.simulate;

import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_SIMULATE_EXIT_POOL;

import com.fulldive.wallet.models.BaseChain;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.DeterministicKey;

import java.math.BigInteger;

import cosmos.auth.v1beta1.QueryGrpc;
import cosmos.auth.v1beta1.QueryOuterClass;
import cosmos.tx.v1beta1.ServiceGrpc;
import cosmos.tx.v1beta1.ServiceOuterClass;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.cosmos.Signer;
import wannabit.io.cosmostaion.crypto.CryptoHelper;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.model.type.Fee;
import wannabit.io.cosmostaion.network.ChannelBuilder;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WKey;
import wannabit.io.cosmostaion.utils.WLog;

public class SimulOsmosisExitPoolGrpcTask extends CommonTask {

    private final Account mAccount;
    private final BaseChain mBaseChain;
    private final long mPoolId;
    private final String mShareAmount;
    private final String mMemo;
    private final Coin mWithdraw0Coin;
    private final Coin mWithdraw1Coin;
    private final Fee mFees;
    private final String mChainId;

    private QueryOuterClass.QueryAccountResponse mAuthResponse;
    private ECKey ecKey;

    public SimulOsmosisExitPoolGrpcTask(BaseApplication app, TaskListener listener, Account account, BaseChain basechain, long poolId, Coin Withdraw0Coin, Coin Withdraw1Coin, String shareAmount, String memo, Fee fee, String chainId) {
        super(app, listener);
        this.mAccount = account;
        this.mBaseChain = basechain;
        this.mPoolId = poolId;
        this.mWithdraw0Coin = Withdraw0Coin;
        this.mWithdraw1Coin = Withdraw1Coin;
        this.mShareAmount = shareAmount;
        this.mFees = fee;
        this.mMemo = memo;
        this.mChainId = chainId;
        this.result.taskType = TASK_GRPC_SIMULATE_EXIT_POOL;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            if (mAccount.fromMnemonic) {
                String entropy = CryptoHelper.doDecryptData(context.getString(R.string.key_mnemonic) + mAccount.uuid, mAccount.resource, mAccount.spec);
                DeterministicKey deterministicKey = WKey.getKeyWithPathfromEntropy(mAccount, entropy);
                ecKey = ECKey.fromPrivate(new BigInteger(deterministicKey.getPrivateKeyAsHex(), 16));
            } else {
                String privateKey = CryptoHelper.doDecryptData(context.getString(R.string.key_private) + mAccount.uuid, mAccount.resource, mAccount.spec);
                ecKey = ECKey.fromPrivate(new BigInteger(privateKey, 16));
            }

            QueryGrpc.QueryBlockingStub authStub = QueryGrpc.newBlockingStub(ChannelBuilder.getChain(mBaseChain));
            QueryOuterClass.QueryAccountRequest request = QueryOuterClass.QueryAccountRequest.newBuilder().setAddress(mAccount.address).build();
            mAuthResponse = authStub.account(request);

            //broadCast
            ServiceGrpc.ServiceBlockingStub txService = ServiceGrpc.newBlockingStub(ChannelBuilder.getChain(mBaseChain));
            ServiceOuterClass.SimulateRequest simulateTxRequest = Signer.getGrpcExitPoolSimulateReq(mAuthResponse, mPoolId, mWithdraw0Coin, mWithdraw1Coin, mShareAmount, mFees, mMemo, ecKey, mChainId);
            ServiceOuterClass.SimulateResponse response = txService.simulate(simulateTxRequest);
            result.resultData = response.getGasInfo();
            result.isSuccess = true;

        } catch (Exception e) {
            WLog.e("SimulOsmosisExitPoolGrpcTask " + e.getMessage());
            result.isSuccess = false;
        }
        return result;
    }
}

