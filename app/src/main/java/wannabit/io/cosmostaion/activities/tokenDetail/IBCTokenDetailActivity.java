package wannabit.io.cosmostaion.activities.tokenDetail;

import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_IBC_TRANSFER;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_SEND;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulldive.wallet.models.Currency;
import com.fulldive.wallet.presentation.accounts.AccountShowDialogFragment;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.SendActivity;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.dao.IbcToken;
import wannabit.io.cosmostaion.dao.Price;
import wannabit.io.cosmostaion.dialog.Dialog_IBC_Send_Warning;
import wannabit.io.cosmostaion.dialog.Dialog_WatchMode;
import wannabit.io.cosmostaion.utils.PriceProvider;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WUtil;

public class IBCTokenDetailActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView mToolbarSymbolImg;
    private TextView mToolbarSymbol;
    private TextView mToolbarChannel;
    private TextView mItemPerPrice;
    private ImageView mItemUpDownImg;
    private TextView mItemUpDownPrice;

    private CardView mBtnAddressPopup;
    private ImageView mKeyState;
    private TextView mAddress;
    private TextView mTotalValue;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private RelativeLayout mBtnIbcSend;
    private RelativeLayout mBtnSend;

    private IBCTokenAdapter mAdapter;

    private String mIbcDenom;
    private IbcToken mIbcToken;
    private BigDecimal mMaxAvailable = BigDecimal.ZERO;
    private int mIbcDivideDecimal = 6;
    private int mIbcDisplayDecimal = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibc_token_detail);
        mToolbar = findViewById(R.id.toolbar);
        mToolbarSymbolImg = findViewById(R.id.toolbar_symbol_img);
        mToolbarSymbol = findViewById(R.id.toolbar_symbol);
        mToolbarChannel = findViewById(R.id.toolbar_channel);
        mItemPerPrice = findViewById(R.id.per_price);
        mItemUpDownImg = findViewById(R.id.ic_price_updown);
        mItemUpDownPrice = findViewById(R.id.dash_price_updown_tx);

        mBtnAddressPopup = findViewById(R.id.cardView);
        mKeyState = findViewById(R.id.img_account);
        mAddress = findViewById(R.id.account_Address);
        mTotalValue = findViewById(R.id.total_value);
        mSwipeRefreshLayout = findViewById(R.id.layer_refresher);
        mRecyclerView = findViewById(R.id.recycler);
        mBtnIbcSend = findViewById(R.id.btn_ibc_send);
        mBtnSend = findViewById(R.id.btn_send);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIbcDenom = getIntent().getStringExtra("denom");
        mIbcToken = getBaseDao().getIbcToken(mIbcDenom);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onUpdateView();
            }
        });

        if (mIbcToken != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new IBCTokenAdapter();
            mRecyclerView.setAdapter(mAdapter);

            onUpdateView();
        } else {
            onForceBack();
        }
        mBtnAddressPopup.setOnClickListener(this);
        mBtnIbcSend.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onUpdateView() {
        final String baseDenom = getBaseDao().getBaseDenom(mIbcDenom);
        final Currency currency = settingsInteractor.getCurrency();
        final PriceProvider priceProvider = this::getPrice;
        if (mIbcToken == null) {
            mToolbarSymbolImg.setImageResource(R.drawable.token_default_ibc);
            mToolbarSymbol.setText(R.string.str_unknown);
            mToolbarSymbol.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

        } else {
            if (mIbcToken.auth) {
                mIbcDivideDecimal = mIbcToken.decimal;
                try {
                    Picasso.get().load(mIbcToken.moniker).fit().placeholder(R.drawable.token_default_ibc).error(R.drawable.token_default_ibc).into(mToolbarSymbolImg);
                } catch (Exception e) {
                }
                mToolbarSymbol.setText(mIbcToken.display_denom.toUpperCase());
                mToolbarSymbol.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                mTotalValue.setText("" + WDp.dpUserCurrencyValue(getBaseDao(), currency, baseDenom, getBalance(mIbcDenom), mIbcDivideDecimal, priceProvider));

                mItemPerPrice.setText(WDp.dpPerUserCurrencyValue(getBaseDao(), currency, baseDenom, priceProvider));

                final Price price = getPrice(baseDenom);
                mItemUpDownPrice.setText(WDp.dpValueChange(price));
                final BigDecimal lastUpDown = WDp.valueChange(price);
                if (lastUpDown.compareTo(BigDecimal.ZERO) > 0) {
                    mItemUpDownImg.setVisibility(View.VISIBLE);
                    mItemUpDownImg.setImageResource(R.drawable.ic_price_up);
                } else if (lastUpDown.compareTo(BigDecimal.ZERO) < 0) {
                    mItemUpDownImg.setVisibility(View.VISIBLE);
                    mItemUpDownImg.setImageResource(R.drawable.ic_price_down);
                } else {
                    mItemUpDownImg.setVisibility(View.INVISIBLE);
                }

            } else {
                mToolbarSymbolImg.setImageResource(R.drawable.token_default_ibc);
                mToolbarSymbol.setText(R.string.str_unknown);
                mToolbarSymbol.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                mTotalValue.setText(WDp.dpUserCurrencyValue(getBaseDao(), currency, baseDenom, BigDecimal.ZERO, mIbcDivideDecimal, priceProvider));

                mItemPerPrice.setText("");
                mItemUpDownPrice.setText("");
                mItemUpDownImg.setVisibility(View.INVISIBLE);
            }
        }

        mToolbarChannel.setText("(" + mIbcToken.channel_id + ")");
        mBtnAddressPopup.setCardBackgroundColor(WDp.getChainBgColor(IBCTokenDetailActivity.this, getBaseChain()));
        mAddress.setText(getAccount().address);
        mKeyState.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGray0), android.graphics.PorterDuff.Mode.SRC_IN);
        if (getAccount().hasPrivateKey) {
            mKeyState.setColorFilter(WDp.getChainColor(getBaseContext(), getBaseChain()), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnAddressPopup)) {
            AccountShowDialogFragment show = AccountShowDialogFragment.Companion.newInstance(
                    getAccount().getAccountTitle(this),
                    getAccount().address
            );
            showDialog(show);

        } else if (v.equals(mBtnIbcSend)) {
            if (!getAccount().hasPrivateKey) {
                Dialog_WatchMode add = Dialog_WatchMode.newInstance();
                showDialog(add);
                return;
            }
            final String mainDenom = getBaseChain().getMainDenom();
            final BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_IBC_TRANSFER);

            mMaxAvailable = getBalance(mainDenom).subtract(feeAmount);
            if (mMaxAvailable.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_budget, Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("sendTokenDenom", mIbcDenom);
            Dialog_IBC_Send_Warning warning = Dialog_IBC_Send_Warning.newInstance(bundle);
            showDialog(warning);

        } else if (v.equals(mBtnSend)) {
            if (!getAccount().hasPrivateKey) {
                Dialog_WatchMode add = Dialog_WatchMode.newInstance();
                showDialog(add);
                return;
            }
            Intent intent = new Intent(getBaseContext(), SendActivity.class);
            BigDecimal mainAvailable = getBalance(getBaseChain().getMainDenom());
            BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_SEND);
            if (mainAvailable.compareTo(feeAmount) < 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra("sendTokenDenom", mIbcDenom);
            startActivity(intent);
        }
    }

    private void onForceBack() {
        new Handler().postDelayed(() -> onBackPressed(), 300);
    }

    private class IBCTokenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_IBC_STATUS = 1;
        private static final int TYPE_HISTORY = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_IBC_STATUS) {
                return new IbcStatusHolder(getLayoutInflater().inflate(R.layout.item_ibc_token_status, viewGroup, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (getItemViewType(position) == TYPE_IBC_STATUS) {
                onBindIbcInfo(viewHolder);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_IBC_STATUS;
            } else {
                return TYPE_HISTORY;
            }
        }

        private void onBindIbcInfo(RecyclerView.ViewHolder viewHolder) {
            final IbcStatusHolder holder = (IBCTokenAdapter.IbcStatusHolder) viewHolder;
            final BigDecimal totalAmount = getBalance(mIbcDenom);
            if (mIbcToken.auth) {
                mIbcDivideDecimal = mIbcToken.decimal;
                mIbcDisplayDecimal = mIbcToken.decimal;
            }
            try {
                Picasso.get().load(getBaseDao().getIbcRelayerImg(getBaseChain(), mIbcToken.channel_id)).into(holder.itemRelayer);
            } catch (Exception e) {
            }
            holder.itemCurrentAmount.setText(WDp.getDpAmount2(totalAmount, mIbcDivideDecimal, mIbcDisplayDecimal));
            holder.itemCurrentDenom.setText("ibc/" + mIbcToken.hash);

            holder.itemAcrossChain.setText(mIbcToken.counter_party.chain_id);
            holder.itemAcrossDenom.setText(mIbcToken.base_denom);
        }

        public class IbcStatusHolder extends RecyclerView.ViewHolder {
            private final ImageView itemRelayer;
            private final TextView itemCurrentAmount;
            private final TextView itemCurrentDenom;
            private final TextView itemAcrossChain;
            private final TextView itemAcrossDenom;

            public IbcStatusHolder(View v) {
                super(v);
                itemRelayer = itemView.findViewById(R.id.img_relayer);
                itemCurrentAmount = itemView.findViewById(R.id.current_amount);
                itemCurrentDenom = itemView.findViewById(R.id.current_denom);
                itemAcrossChain = itemView.findViewById(R.id.across_chain);
                itemAcrossDenom = itemView.findViewById(R.id.across_denom);
            }
        }
    }
}
