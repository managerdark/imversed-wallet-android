package wannabit.io.cosmostaion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.fulldive.wallet.presentation.accounts.restore.MnemonicRestoreActivity;

import wannabit.io.cosmostaion.R;

public class Dialog_OkexRestoreType extends DialogFragment {

    public static Dialog_OkexRestoreType newInstance() {
        return new Dialog_OkexRestoreType();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_okex_restore_type, null);

        view.findViewById(R.id.oldPathLayout).setOnClickListener(v -> {
            ((MnemonicRestoreActivity) getActivity()).onUsingCustomPath(0);
            dismiss();
        });

        view.findViewById(R.id.newPathLayout).setOnClickListener(v -> {
            ((MnemonicRestoreActivity) getActivity()).onUsingCustomPath(1);
            dismiss();
        });

        view.findViewById(R.id.ethPathLayout).setOnClickListener(v -> {
            ((MnemonicRestoreActivity) getActivity()).onUsingCustomPath(2);
            dismiss();
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
