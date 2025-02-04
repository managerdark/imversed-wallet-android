package wannabit.io.cosmostaion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.fulldive.wallet.presentation.accounts.restore.PrivateKeyRestoreActivity;

import wannabit.io.cosmostaion.R;

public class Dialog_Choice_Type_OKex extends DialogFragment {

    public static Dialog_Choice_Type_OKex newInstance() {
        Dialog_Choice_Type_OKex frag = new Dialog_Choice_Type_OKex();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_choice_type_okex, null);
        LinearLayout mOldAddress = view.findViewById(R.id.old_address);
        LinearLayout mNewAddress = view.findViewById(R.id.new_address);

        mOldAddress.setOnClickListener(v -> {
            ((PrivateKeyRestoreActivity) getActivity()).onCheckOecAddressType(0);
            dismiss();
        });

        mNewAddress.setOnClickListener(v -> {
            ((PrivateKeyRestoreActivity) getActivity()).onCheckOecAddressType(1);
            dismiss();
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}