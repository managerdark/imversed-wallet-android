package wannabit.io.cosmostaion.task.gRpcTask.simulate;

import static com.fulldive.wallet.models.BaseChain.CRYPTO_MAIN;
import static com.fulldive.wallet.models.BaseChain.IRIS_MAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_SIMULATE_TRANSFER_NFT;

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
import wannabit.io.cosmostaion.model.type.Fee;
import wannabit.io.cosmostaion.network.ChannelBuilder;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WKey;
import wannabit.io.cosmostaion.utils.WLog;

public class SimulTransferNFTGrpcTask extends CommonTask {

    private final Account mAccount;
    private final BaseChain mBaseChain;
    private final String mRecipient;
    private final String mDenomId;
    private final String mId;
    private final irismod.nft.QueryOuterClass.QueryNFTResponse mIrisResponse;
    private final Fee mFees;
    private final String mMemo;
    private final String mChainId;

    private QueryOuterClass.QueryAccountResponse mAuthResponse;
    private ECKey ecKey;

    public SimulTransferNFTGrpcTask(BaseApplication app, TaskListener listener, Account account, BaseChain basechain, String sender, String recipient, String denomId, String id,
                                    irismod.nft.QueryOuterClass.QueryNFTResponse irisResponse, String memo, Fee fee, String chainId) {
        super(app, listener);
        this.mAccount = account;
        this.mBaseChain = basechain;
        this.mAccount.address = sender;
        this.mRecipient = recipient;
        this.mDenomId = denomId;
        this.mId = id;
        this.mIrisResponse = irisResponse;
        this.mMemo = memo;
        this.mFees = fee;
        this.mChainId = chainId;
        this.result.taskType = TASK_GRPC_SIMULATE_TRANSFER_NFT;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            if (mAccount.fromMnemonic) {
                String entropy = CryptoHelper.decryptData(context.getString(R.string.key_mnemonic) + mAccount.uuid, mAccount.resource, mAccount.spec);
                DeterministicKey deterministicKey = WKey.getKeyWithPathfromEntropy(mAccount, entropy);
                ecKey = ECKey.fromPrivate(new BigInteger(deterministicKey.getPrivateKeyAsHex(), 16));
            } else {
                String privateKey = CryptoHelper.decryptData(context.getString(R.string.key_private) + mAccount.uuid, mAccount.resource, mAccount.spec);
                ecKey = ECKey.fromPrivate(new BigInteger(privateKey, 16));
            }

            //simulate
            QueryGrpc.QueryBlockingStub authStub = QueryGrpc.newBlockingStub(ChannelBuilder.getChain(mBaseChain));
            QueryOuterClass.QueryAccountRequest request = QueryOuterClass.QueryAccountRequest.newBuilder().setAddress(mAccount.address).build();
            mAuthResponse = authStub.account(request);

            ServiceOuterClass.SimulateRequest simulateTxRequest = null;
            ServiceGrpc.ServiceBlockingStub txService = ServiceGrpc.newBlockingStub(ChannelBuilder.getChain(mBaseChain));
            if (mBaseChain.equals(IRIS_MAIN.INSTANCE)) {
                simulateTxRequest = Signer.getGrpcSendNftIrisSimulateReq(mAuthResponse, mAccount.address, mRecipient, mDenomId, mId, mIrisResponse, mFees, mMemo, ecKey, mChainId);
            } else if (mBaseChain.equals(CRYPTO_MAIN.INSTANCE)) {
                simulateTxRequest = Signer.getGrpcSendNftCroSimulateReq(mAuthResponse, mAccount.address, mRecipient, mDenomId, mId, mFees, mMemo, ecKey, mChainId);
            }
            if (simulateTxRequest != null) {
                ServiceOuterClass.SimulateResponse response = txService.simulate(simulateTxRequest);
                result.resultData = response.getGasInfo();
                result.isSuccess = true;
            }

        } catch (Exception e) {
            WLog.e("SimulTransferNFTGrpcTask " + e.getMessage());
            result.isSuccess = false;
        }
        return result;
    }

}
