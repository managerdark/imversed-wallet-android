package wannabit.io.cosmostaion.task.gRpcTask;

import static cosmos.staking.v1beta1.Staking.BondStatus.BOND_STATUS_BONDED;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_BONDED_VALIDATORS;
import static wannabit.io.cosmostaion.network.ChannelBuilder.TIME_OUT;

import com.fulldive.wallet.models.BaseChain;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cosmos.base.query.v1beta1.Pagination;
import cosmos.staking.v1beta1.QueryGrpc;
import cosmos.staking.v1beta1.QueryOuterClass;
import cosmos.staking.v1beta1.Staking;
import kotlin.Deprecated;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.network.ChannelBuilder;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;

@Deprecated(message = "Migrate to rx")
public class BondedValidatorsGrpcTask extends CommonTask {
    private final ArrayList<Staking.Validator> mResultData = new ArrayList<>();
    private final QueryGrpc.QueryBlockingStub mStub;

    public BondedValidatorsGrpcTask(BaseApplication app, TaskListener listener, BaseChain chain) {
        super(app, listener);
        this.result.taskType = TASK_GRPC_FETCH_BONDED_VALIDATORS;
        this.mStub = QueryGrpc.newBlockingStub(ChannelBuilder.getChain(chain)).withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS);
    }


    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            Pagination.PageRequest pageRequest = Pagination.PageRequest.newBuilder().setLimit(300).build();
            QueryOuterClass.QueryValidatorsRequest request = QueryOuterClass.QueryValidatorsRequest.newBuilder().setPagination(pageRequest).setStatus(BOND_STATUS_BONDED.name()).build();
            QueryOuterClass.QueryValidatorsResponse response = mStub.validators(request);
            mResultData.addAll(response.getValidatorsList());

            this.result.isSuccess = true;
            this.result.resultData = mResultData;
//            WLog.w("BondedValidatorsGrpcTask " + mResultData.size());


//            QueryOuterClass.QueryValidatorsRequest request = QueryOuterClass.QueryValidatorsRequest.newBuilder().setStatus(BOND_STATUS_BONDED.name()).build();
//            QueryOuterClass.QueryValidatorsResponse response = mStub.validators(request);
//            mResultData.addAll(response.getValidatorsList());
//            WLog.w("BondedValidators init");
//
//            if (response.hasPagination() && response.getPagination().getNextKey().size() > 0) {
//                pageJob(response.getPagination().getNextKey());
//                WLog.w("pageJob pageJob");
//            }
//            this.mResult.isSuccess = true;
//            this.mResult.resultData = mResultData;
//            WLog.w("BondedValidators " + mResultData.size());


        } catch (Exception e) {
            WLog.e("BondedValidatorsGrpcTask " + e.getMessage());
        }
        return result;
    }

    private QueryOuterClass.QueryValidatorsResponse pageJob(com.google.protobuf.ByteString nextKey) {
        try {
            Pagination.PageRequest pageRequest = Pagination.PageRequest.newBuilder().setKey(nextKey).setLimit(500).build();
            QueryOuterClass.QueryValidatorsRequest request = QueryOuterClass.QueryValidatorsRequest.newBuilder().setPagination(pageRequest).setStatus(BOND_STATUS_BONDED.name()).build();
            QueryOuterClass.QueryValidatorsResponse response = mStub.validators(request);
            mResultData.addAll(response.getValidatorsList());
            if (response.hasPagination() && response.getPagination().getNextKey().size() > 0) {
                pageJob(response.getPagination().getNextKey());
            }
        } catch (Exception e) {
            WLog.e("BondedValidatorsGrpcTask pageJob " + e.getMessage());
        }
        return null;
    }
}
