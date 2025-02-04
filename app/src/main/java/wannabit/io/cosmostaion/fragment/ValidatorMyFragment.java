package wannabit.io.cosmostaion.fragment;

import static com.fulldive.wallet.models.BaseChain.BAND_MAIN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.List;

import cosmos.staking.v1beta1.Staking;
import de.hdodenhof.circleimageview.CircleImageView;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.ValidatorListActivity;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.base.IBusyFetchListener;
import wannabit.io.cosmostaion.base.IRefreshTabListener;
import wannabit.io.cosmostaion.dialog.Dialog_My_ValidatorSorting;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WUtil;

public class ValidatorMyFragment extends BaseFragment implements View.OnClickListener, IRefreshTabListener, IBusyFetchListener {

    public final static int SELECT_MY_VALIDATOR_SORTING = 6003;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyValidatorAdapter mMyValidatorAdapter;
    private TextView mValidatorSize, mSortType;
    private LinearLayout mBtnSort;

    public static ValidatorMyFragment newInstance(Bundle bundle) {
        ValidatorMyFragment fragment = new ValidatorMyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validator_my, container, false);
        mSwipeRefreshLayout = rootView.findViewById(R.id.layer_refresher);
        mRecyclerView = rootView.findViewById(R.id.recycler);
        mValidatorSize = rootView.findViewById(R.id.validator_cnt);
        mSortType = rootView.findViewById(R.id.token_sort_type);
        mBtnSort = rootView.findViewById(R.id.btn_validator_sort);

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getMainActivity().onFetchAllData();
            mMyValidatorAdapter.notifyDataSetChanged();
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mMyValidatorAdapter = new MyValidatorAdapter();
        mRecyclerView.setAdapter(mMyValidatorAdapter);
        mBtnSort.setOnClickListener(this);
        onRefreshTab();
        return rootView;
    }

    @Override
    public void onRefreshTab() {
        if (!isAdded()) return;
        if (getMainActivity().getBaseChain().isGRPC()) {
            mValidatorSize.setText("" + getBaseDao().mGRpcMyValidators.size());
        } else {
            mValidatorSize.setText("" + getBaseDao().getMyValidators().size());
        }
        onSortValidator();
        mMyValidatorAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBusyFetch() {
        if (!isAdded()) return;
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public ValidatorListActivity getMainActivity() {
        return (ValidatorListActivity) getBaseActivity();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnSort)) {
            onShowMyValidatorSort();
        }
    }


    private class MyValidatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_MY_VALIDATOR = 1;
        private static final int TYPE_PROMOTION = 2;
        private static final int TYPE_HEADER_WITHDRAW_ALL = 3;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_MY_VALIDATOR) {
                return new RewardMyValidatorHolder(getLayoutInflater().inflate(R.layout.item_reward_my_validator, viewGroup, false));
            } else if (viewType == TYPE_PROMOTION) {
                return new RewardPromotionHolder(getLayoutInflater().inflate(R.layout.item_reward_promotion, viewGroup, false));
            } else if (viewType == TYPE_HEADER_WITHDRAW_ALL) {
                return new RewardWithdrawHolder(getLayoutInflater().inflate(R.layout.item_reward_withdraw_all, viewGroup, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
            if (getItemViewType(position) == TYPE_PROMOTION) {
                RewardPromotionHolder holder = (RewardPromotionHolder) viewHolder;
                holder.itemRoot.setCardBackgroundColor(WDp.getChainBgColor(getMainActivity(), getMainActivity().getBaseChain()));

                holder.itemRoot.setOnClickListener(v -> getMainActivity().onStartDelegate());

            } else if (getItemViewType(position) == TYPE_HEADER_WITHDRAW_ALL) {
                final RewardWithdrawHolder holder = (RewardWithdrawHolder) viewHolder;
                WDp.DpMainDenom(getMainActivity().getAccount().baseChain, holder.itemTvDenom);
                final int dpDecimal = getMainActivity().getBaseChain().getDivideDecimal();
                if (getMainActivity().getBaseChain().isGRPC()) {
                    final BigDecimal allRewardAmount = getBaseDao().getRewardSum(getMainActivity().getBaseChain().getMainDenom());
                    holder.itemTvAllRewards.setText(WDp.getDpAmount2(allRewardAmount, dpDecimal, 6));

                } else {
                    final BigDecimal allRewardAmount = getBaseDao().rewardAmount(getMainActivity().getBaseChain().getMainDenom());
                    holder.itemTvAllRewards.setText(WDp.getDpAmount2(allRewardAmount, dpDecimal, 6));
                }

                holder.itemBtnWithdrawAll.setOnClickListener(v -> getMainActivity().onStartRewardAll());

            } else if (getItemViewType(position) == TYPE_MY_VALIDATOR) {
                final RewardMyValidatorHolder holder = (RewardMyValidatorHolder) viewHolder;
                holder.itemBandOracleOff.setVisibility(View.INVISIBLE);
                final int dpDecimal = getMainActivity().getBaseChain().getDivideDecimal();
                if (getMainActivity().getBaseChain().isGRPC()) {
                    final Staking.Validator validator = getBaseDao().mGRpcMyValidators.get(position);
                    final BigDecimal delegationAmount = getBaseDao().getDelegation(validator.getOperatorAddress());
                    final BigDecimal undelegationAmount = getBaseDao().getUndelegation(validator.getOperatorAddress());
                    final BigDecimal rewardAmount = getBaseDao().getReward(getMainActivity().getBaseChain().getMainDenom(), validator.getOperatorAddress());
                    try {
                        Picasso.get().load(getMainActivity().getBaseChain().getMonikerImageLink(validator.getOperatorAddress())).fit().placeholder(R.drawable.validator_none_img).error(R.drawable.validator_none_img).into(holder.itemAvatar);
                    } catch (Exception e) {
                    }

                    holder.itemTvMoniker.setText(validator.getDescription().getMoniker());
                    holder.itemRoot.setCardBackgroundColor(WDp.getChainBgColor(getMainActivity(), getMainActivity().getBaseChain()));
                    holder.itemTvDelegateAmount.setText(WDp.getDpAmount2(delegationAmount, dpDecimal, 6));
                    holder.itemTvUndelegateAmount.setText(WDp.getDpAmount2(undelegationAmount, dpDecimal, 6));
                    holder.itemTvReward.setText(WDp.getDpAmount2(rewardAmount, dpDecimal, 6));

                    if (validator.getJailed()) {
                        holder.itemAvatar.setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorRed));
                        holder.itemRevoked.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemAvatar.setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorGray3));
                        holder.itemRevoked.setVisibility(View.GONE);
                    }
                    holder.itemRoot.setOnClickListener(v -> getMainActivity().onStartValidatorDetailV1(validator.getOperatorAddress()));

                    if (getMainActivity().getBaseChain().equals(BAND_MAIN.INSTANCE)) {
                        if (getBaseDao().mChainParam != null && !getBaseDao().mChainParam.isOracleEnable(validator.getOperatorAddress())) {
                            holder.itemBandOracleOff.setVisibility(View.VISIBLE);
                        } else {
                            holder.itemBandOracleOff.setVisibility(View.INVISIBLE);
                        }
                    }

                } else {
                    final Validator validator = getBaseDao().getMyValidators().get(position);
                    final BigDecimal delegationAmount = getBaseDao().delegatedAmountByValidator(validator.operator_address);
                    final BigDecimal undelegationAmount = getBaseDao().unbondingAmountByValidator(validator.operator_address);
                    final BigDecimal rewardAmount = getBaseDao().rewardAmountByValidator(getMainActivity().getBaseChain().getMainDenom(), validator.operator_address);
                    try {
                        Picasso.get().load(getMainActivity().getBaseChain().getMonikerImageLink(validator.operator_address)).fit().placeholder(R.drawable.validator_none_img).error(R.drawable.validator_none_img).into(holder.itemAvatar);
                    } catch (Exception e) {
                    }

                    holder.itemRoot.setCardBackgroundColor(WDp.getChainBgColor(getMainActivity(), getMainActivity().getBaseChain()));
                    holder.itemTvMoniker.setText(validator.description.moniker);
                    holder.itemTvDelegateAmount.setText(WDp.getDpAmount2(delegationAmount, dpDecimal, 6));
                    holder.itemTvUndelegateAmount.setText(WDp.getDpAmount2(undelegationAmount, dpDecimal, 6));
                    holder.itemTvReward.setText(WDp.getDpAmount2(rewardAmount, dpDecimal, 6));

                    if (validator.jailed) {
                        holder.itemAvatar.setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorRed));
                        holder.itemRevoked.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemAvatar.setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorGray3));
                        holder.itemRevoked.setVisibility(View.GONE);
                    }
                    holder.itemRoot.setOnClickListener(v -> getMainActivity().onStartValidatorDetail(validator));
                }
            }
        }

        @Override
        public int getItemCount() {
            if (getMainActivity().getBaseChain().isGRPC()) {
                if (getBaseDao().mGRpcMyValidators == null || getBaseDao().mGRpcMyValidators.size() < 1) {
                    return 1;
                } else if (getBaseDao().mGRpcMyValidators.size() == 1) {
                    return 1;
                } else if (getBaseDao().mGRpcMyValidators.size() >= 1) {
                    return getBaseDao().mGRpcMyValidators.size() + 1;
                }

            } else {
                return getBaseDao().getMyValidators().size() + 1;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (getMainActivity().getBaseChain().isGRPC()) {
                if (getBaseDao().mGRpcMyValidators == null || getBaseDao().mGRpcMyValidators.size() < 1) {
                    return TYPE_PROMOTION;
                } else if (getBaseDao().mGRpcMyValidators.size() > 1 && position == getBaseDao().mGRpcMyValidators.size()) {
                    return TYPE_HEADER_WITHDRAW_ALL;
                } else {
                    return TYPE_MY_VALIDATOR;
                }

            } else {
                List<Validator> items = getBaseDao().getMyValidators();

                if (items.size() < 1) {
                    return TYPE_PROMOTION;
                } else if (items.size() > 1 && position == items.size()) {
                    return TYPE_HEADER_WITHDRAW_ALL;
                } else {
                    return TYPE_MY_VALIDATOR;
                }

            }
        }


        public class RewardMyValidatorHolder extends RecyclerView.ViewHolder {
            CardView itemRoot;
            CircleImageView itemAvatar;
            ImageView itemFree;
            ImageView itemRevoked, itemBandOracleOff;
            TextView itemTvMoniker;
            TextView itemTvDelegateAmount;
            TextView itemTvUndelegateAmount;
            TextView itemTvReward;

            public RewardMyValidatorHolder(@NonNull View itemView) {
                super(itemView);
                itemRoot = itemView.findViewById(R.id.card_validator);
                itemAvatar = itemView.findViewById(R.id.avatar_validator);
                itemFree = itemView.findViewById(R.id.avatar_validator_free);
                itemRevoked = itemView.findViewById(R.id.avatar_validator_revoke);
                itemTvMoniker = itemView.findViewById(R.id.moniker_validator);
                itemBandOracleOff = itemView.findViewById(R.id.band_oracle_off);
                itemTvDelegateAmount = itemView.findViewById(R.id.delegate_amount_validator);
                itemTvUndelegateAmount = itemView.findViewById(R.id.undelegate_amount_validator);
                itemTvReward = itemView.findViewById(R.id.my_reward_validator);
            }
        }

        public class RewardPromotionHolder extends RecyclerView.ViewHolder {
            CardView itemRoot;
            Button itemBtnDelegateStart;

            public RewardPromotionHolder(@NonNull View itemView) {
                super(itemView);
                itemRoot = itemView.findViewById(R.id.card_validator);
                itemBtnDelegateStart = itemView.findViewById(R.id.btn_start_delegate);
            }
        }

        public class RewardWithdrawHolder extends RecyclerView.ViewHolder {
            TextView itemTvAllRewards, itemTvDenom;
            Button itemBtnWithdrawAll;

            public RewardWithdrawHolder(@NonNull View itemView) {
                super(itemView);
                itemTvAllRewards = itemView.findViewById(R.id.tx_all_rewards);
                itemTvDenom = itemView.findViewById(R.id.tx_demon);
                itemBtnWithdrawAll = itemView.findViewById(R.id.btn_withdraw_all);
            }
        }

    }

    public void onSortValidator() {
        final BaseData baseDao = getBaseDao();
        if (getMainActivity().getBaseChain().isGRPC()) {
            if (baseDao.getMyValSorting() == 2) {
                mSortType.setText(getString(R.string.str_sorting_by_reward));
                baseDao.mGRpcMyValidators = WUtil.onSortByRewardV1(baseDao.mGRpcMyValidators, getMainActivity().getBaseChain().getMainDenom(), baseDao);

            } else if (baseDao.getMyValSorting() == 0) {
                baseDao.mGRpcMyValidators = WUtil.onSortByValidatorNameV1(baseDao.mGRpcMyValidators);
                mSortType.setText(getString(R.string.str_sorting_by_name));

            } else {
                baseDao.mGRpcMyValidators = WUtil.onSortByDelegateV1(baseDao.mGRpcMyValidators, baseDao);
                mSortType.setText(getString(R.string.str_sorting_by_my_delegated));

            }

        } else {
            if (baseDao.getMyValSorting() == 2) {
                baseDao.mMyValidators = WUtil.onSortByReward(baseDao.getMyValidators(), getMainActivity().getBaseChain().getMainDenom(), baseDao);
                mSortType.setText(getString(R.string.str_sorting_by_reward));

            } else if (baseDao.getMyValSorting() == 0) {
                baseDao.mMyValidators = WUtil.onSortByValidatorName(baseDao.getMyValidators());
                mSortType.setText(getString(R.string.str_sorting_by_name));

            } else {
                baseDao.mMyValidators = WUtil.onSortByDelegate(baseDao.getMyValidators(), baseDao);
                mSortType.setText(getString(R.string.str_sorting_by_my_delegated));
            }
        }
    }

    public void onShowMyValidatorSort() {
        Dialog_My_ValidatorSorting bottomSheetDialog = Dialog_My_ValidatorSorting.getInstance();
        bottomSheetDialog.setArguments(null);
        bottomSheetDialog.setTargetFragment(ValidatorMyFragment.this, SELECT_MY_VALIDATOR_SORTING);
        bottomSheetDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_MY_VALIDATOR_SORTING && resultCode == Activity.RESULT_OK) {
            getBaseDao().setMyValSorting(data.getIntExtra("sorting", 1));
            onRefreshTab();
        }
    }
}
