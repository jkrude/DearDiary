package com.jkrude.deardiary;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolderGeneric> {

    private Context context;
    private Map<Integer, Pair<ViewType, String>> viewTypeMap;
    private SharedPreferences prefs;

    public RecyclerAdapter(Context context, @NonNull Map<Integer, Pair<ViewType, String>> positions) {
        this.context = context;
        this.viewTypeMap = positions;
        prefs = context.getSharedPreferences(
                MainActivity.sharedPrefsTag, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolderGeneric onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (ViewType.fromInt(viewType)) {
            case COUNTER:
                return new ViewHolderCounter(inflater.inflate(R.layout.recycler_row_counter, parent, false));
            case BINARY:
                return new ViewHolderBinary(inflater.inflate(R.layout.recycler_row_binary, parent, false));
            case TEXT_INPUT:
                return new ViewHolderTextInput(inflater.inflate(R.layout.recycler_row_text_input, parent, false));
            case TIME_INPUT:
                return new ViewHolderTime((inflater.inflate(R.layout.recycler_row_time, parent, false)));
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGeneric holder, int position) {
        holder.setName(Objects.requireNonNull(viewTypeMap.get(position)).second);
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.requireNonNull(viewTypeMap.get(position)).first.asInt();
    }

    @Override
    public int getItemCount() {
        return viewTypeMap.size();
    }

    public enum ViewType {
        COUNTER(0),
        BINARY(1),
        TEXT_INPUT(2),
        TIME_INPUT(3);

        private final int code;

        ViewType(int i) {
            code = i;
        }

        public static ViewType fromInt(int i) {
            switch (i) {
                case 0:
                    return COUNTER;
                case 1:
                    return BINARY;
                case 2:
                    return TEXT_INPUT;
                case 3:
                    return TIME_INPUT;
                default:
                    throw new IllegalArgumentException("No EnumType for int: " + i);
            }
        }

        public int asInt() {
            return code;
        }
    }

    // Generic ViewHolder
    public abstract static class ViewHolderGeneric extends RecyclerView.ViewHolder {

        protected String name;

        ViewHolderGeneric(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void setName(String name);
    }

    // Subclasses
    public class ViewHolderCounter extends ViewHolderGeneric {

        int state;
        TextView nameTxtView, stateTxtView;
        ImageButton plusBttn, minusBttn;

        ViewHolderCounter(@NonNull View itemView) {
            super(itemView);
            nameTxtView = itemView.findViewById(R.id.counter_nameTxtView);
            stateTxtView = itemView.findViewById(R.id.counter_stateTxtView);
            plusBttn = itemView.findViewById(R.id.counter_plusBttn);
            minusBttn = itemView.findViewById(R.id.counter_minusBttn);
            stateTxtView.setText(String.valueOf(state));
            plusBttn.setOnClickListener(event -> updateState(1));
            minusBttn.setOnClickListener(event -> updateState(-1));
        }

        @Override
        public void setName(String name) {
            super.name = name;
            nameTxtView.setText(name);
            state = prefs.getInt(name, 0);
            stateTxtView.setText(String.valueOf(state));
        }

        private void updateState(int x) {
            state += x;
            stateTxtView.setText(String.valueOf(state));
            if (name != null) {
                prefs.edit().putInt(name, state).apply();
            } else {
                throw new IllegalStateException("Name was not yet set");
            }
        }
    }


    public class ViewHolderTextInput extends ViewHolderGeneric {

        private TextView nameTextView;
        private EditText editText;

        public ViewHolderTextInput(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_txtView);
            editText = itemView.findViewById(R.id.text_textInput);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    prefs.edit().putString(name, s.toString()).apply();
                }

            });
            // Fix for "Focus search returned a view that wasn't able to take focus"
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        @Override
        public void setName(String name) {
            super.name = name;
            nameTextView.setText(name);
            editText.setText(prefs.getString(name, ""));
        }
    }


    public class ViewHolderBinary extends ViewHolderGeneric {

        private TextView nameTxtView;
        private CheckBox checkBox;

        ViewHolderBinary(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.binary_checkBox);
            checkBox.setOnClickListener(v -> {
                if (name != null) {
                    prefs.edit().putBoolean(name, checkBox.isChecked()).apply();
                } else {
                    throw new IllegalStateException("name was not yet set");
                }
            });
            nameTxtView = itemView.findViewById(R.id.binary_nameTxtView);
        }

        @Override
        public void setName(String name) {
            super.name = name;
            nameTxtView.setText(name);
            checkBox.setChecked(prefs.getBoolean(name, false));
        }
    }

    public class ViewHolderTime extends ViewHolderGeneric implements TimePickerDialog.OnTimeSetListener {

        private static final String hourOfDayTag = "hourOfDay";
        private static final String minuteTag = "minute";

        private TextView nameTxtView;
        private TextView timeInput;

        ViewHolderTime(@NonNull View itemView) {
            super(itemView);
            nameTxtView = itemView.findViewById(R.id.recycler_time_name);
            timeInput = itemView.findViewById(R.id.recycler_time_timeView);
            ImageButton btn = itemView.findViewById(R.id.recycler_time_btn);
            btn.setOnClickListener(v ->
            {
                DialogFragment timePicker = new TimePickerFragment(this);
                timePicker.show(((MainActivity) context).getSupportFragmentManager(), " TP");
            });
        }


        @Override
        public void setName(String name) {
            super.name = name;
            nameTxtView.setText(name);
            int hourOfDay = prefs.getInt(name + hourOfDayTag, 0);
            int minute = prefs.getInt(name + minuteTag, 0);
            setLabelText(hourOfDay, minute);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            prefs.edit().putInt(name + hourOfDayTag, hourOfDay).apply();
            prefs.edit().putInt(name + minuteTag, minute).apply();
            setLabelText(hourOfDay, minute);
        }

        private void setLabelText(int hourOfDay, int minute) {
            String txt;
            txt = hourOfDay >= 10 ? String.valueOf(hourOfDay) : "0" + String.valueOf(hourOfDay);
            txt += ":";
            txt += minute >= 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
            timeInput.setText(txt);
        }
    }
}
