package wannabit.io.cosmostaion.task.gRpcTask;

import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_NFTOKEN_INFO;

import com.fulldive.wallet.models.BaseChain;

import irismod.nft.QueryGrpc;
import irismod.nft.QueryOuterClass;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.network.ChannelBuilder;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;

public class NFTokenInfoGrpcTask extends CommonTask {
    private final BaseChain mChain;
    private final String mDenomId;
    private final String mTokenId;
    private final QueryGrpc.QueryBlockingStub mIrisStub;
    private final chainmain.nft.v1.QueryGrpc.QueryBlockingStub mCryptoStub;

    public NFTokenInfoGrpcTask(BaseApplication app, TaskListener listener, BaseChain chain, String denomId, String tokenId) {
        super(app, listener);
        this.mChain = chain;
        this.mDenomId = denomId;
        this.mTokenId = tokenId;
        this.result.taskType = TASK_GRPC_FETCH_NFTOKEN_INFO;
        this.mIrisStub = QueryGrpc.newBlockingStub(ChannelBuilder.getChain(mChain));
        this.mCryptoStub = chainmain.nft.v1.QueryGrpc.newBlockingStub(ChannelBuilder.getChain(mChain));
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            if (mChain.equals(BaseChain.IRIS_MAIN.INSTANCE)) {
                QueryOuterClass.QueryNFTRequest request = QueryOuterClass.QueryNFTRequest.newBuilder().setDenomId(mDenomId).setTokenId(mTokenId).build();
                QueryOuterClass.QueryNFTResponse response = mIrisStub.nFT(request);

                result.isSuccess = true;
                result.resultData = response;

            } else if (mChain.equals(BaseChain.CRYPTO_MAIN.INSTANCE)) {
                chainmain.nft.v1.QueryOuterClass.QueryNFTRequest request = chainmain.nft.v1.QueryOuterClass.QueryNFTRequest.newBuilder().setDenomId(mDenomId).setTokenId(mTokenId).build();
                chainmain.nft.v1.QueryOuterClass.QueryNFTResponse response = mCryptoStub.nFT(request);

                result.isSuccess = true;
                result.resultData = response.getNft();
            }

        } catch (Exception e) {
            WLog.e("NFTokenInfoGrpcTask " + e.getMessage());
        }
        return result;
    }

}
