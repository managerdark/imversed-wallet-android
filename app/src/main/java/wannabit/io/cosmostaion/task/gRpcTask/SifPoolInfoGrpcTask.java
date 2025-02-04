package wannabit.io.cosmostaion.task.gRpcTask;

import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_SIF_POOL_INFO;
import static wannabit.io.cosmostaion.network.ChannelBuilder.TIME_OUT;

import com.fulldive.wallet.models.BaseChain;

import java.util.concurrent.TimeUnit;

import sifnode.clp.v1.Querier;
import sifnode.clp.v1.QueryGrpc;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.network.ChannelBuilder;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;

public class SifPoolInfoGrpcTask extends CommonTask {
    private final BaseChain mChain;
    private final String mDenom;
    private final QueryGrpc.QueryBlockingStub mStub;

    public SifPoolInfoGrpcTask(BaseApplication app, TaskListener listener, BaseChain chain, String denom) {
        super(app, listener);
        this.mChain = chain;
        this.mDenom = denom;
        this.result.taskType = TASK_GRPC_FETCH_SIF_POOL_INFO;
        this.mStub = QueryGrpc.newBlockingStub(ChannelBuilder.getChain(mChain)).withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            Querier.PoolReq request = Querier.PoolReq.newBuilder().setSymbol(mDenom).build();
            Querier.PoolRes response = mStub.getPool(request);

            result.resultData = response.getPool();
            result.isSuccess = true;

        } catch (Exception e) {
            WLog.e("SifPoolInfoGrpcTask " + e.getMessage());
        }
        return result;
    }
}
