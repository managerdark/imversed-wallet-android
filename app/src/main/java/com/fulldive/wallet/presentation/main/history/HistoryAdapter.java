package com.fulldive.wallet.presentation.main.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.model.type.BnbHistory;
import wannabit.io.cosmostaion.network.res.ResApiNewTxListCustom;
import wannabit.io.cosmostaion.network.res.ResOkHistoryHit;

class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_OLD_HISTORY = 0;
    private static final int TYPE_NEW_HISTORY = 1;
    private final MainActivity mainActivity;
    private final Context context;
    private final BaseChain chain;
    private final BaseData baseData;

    private List<BnbHistory> bnbHistories = new ArrayList<>();
    private List<ResOkHistoryHit> okHistory = new ArrayList<>();
    private List<ResApiNewTxListCustom> apiNewTxCustomHistory = new ArrayList<>();

    public HistoryAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.chain = mainActivity.getBaseChain();
        this.baseData = mainActivity.getBaseDao();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == TYPE_OLD_HISTORY) {
            return new HistoryOldHolder(layoutInflater.inflate(R.layout.item_history, viewGroup, false));
        } else {
            return new HistoryNewHolder(layoutInflater.inflate(R.layout.item_new_history, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (chain.isGRPC()) {
            HistoryNewHolder holder = (HistoryNewHolder) viewHolder;
            final ResApiNewTxListCustom history = apiNewTxCustomHistory.get(position);
            holder.onBindNewHistory(mainActivity, baseData, history);

        } else {
            HistoryOldHolder holder = (HistoryOldHolder) viewHolder;
            if (chain.equals(BaseChain.BNB_MAIN.INSTANCE)) {
                final BnbHistory history = bnbHistories.get(position);
                holder.onBindOldBnbHistory(mainActivity, history);
            } else if (chain.equals(BaseChain.OKEX_MAIN.INSTANCE)) {
                final ResOkHistoryHit history = okHistory.get(position);
                holder.onBindOldOkHistory(mainActivity, history);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chain.isGRPC()) {
            return TYPE_NEW_HISTORY;
        } else {
            return TYPE_OLD_HISTORY;
        }
    }

    @Override
    public int getItemCount() {
        if (chain.equals(BaseChain.BNB_MAIN.INSTANCE)) {
            return bnbHistories.size();
        } else if (chain.equals(BaseChain.OKEX_MAIN.INSTANCE)) {
            return okHistory.size();
        } else {
            return apiNewTxCustomHistory.size();
        }
    }

    public void setBnbHistory(List<BnbHistory> items) {
        bnbHistories = items;
        okHistory = new ArrayList<>();
        apiNewTxCustomHistory = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOkHistory(List<ResOkHistoryHit> items) {
        okHistory = items;
        bnbHistories = new ArrayList<>();
        apiNewTxCustomHistory = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setItems(List<ResApiNewTxListCustom> items) {
        okHistory = new ArrayList<>();
        bnbHistories = new ArrayList<>();
        apiNewTxCustomHistory = items;
        notifyDataSetChanged();
    }
}
