package com.jkrude.deardiary;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jkrude.deardiary.db.Repository;
import com.jkrude.deardiary.db.entities.EntryForDay;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolderGeneric> {

    public static final String LOGTAG = "recycler";
    @NonNull
    Repository rep;
    @NonNull
    private Context context;
    @NonNull
    private List<Pair<String, ViewType>> positions;

    public RecyclerAdapter(
            @NonNull Context context,
            @NonNull Repository rep) {
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context is required to be cast to AppCompatActivity");
        }
        this.context = context;
        this.rep = rep;

        positions = new ArrayList<>();
        for (String name : rep.allBinaryCategories()) {
            positions.add(new Pair<>(name, ViewType.BINARY));
        }
        for (String name : rep.allCounterCategories()) {
            positions.add(new Pair<>(name, ViewType.COUNTER));
        }
        for (String name : rep.allTextCategories()) {
            positions.add(new Pair<>(name, ViewType.TEXT));
        }

        for (String name : rep.allTimeCategories()) {
            positions.add(new Pair<>(name, ViewType.TIME));
        }
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
            case TEXT:
                return new ViewHolderTextInput(inflater.inflate(R.layout.recycler_row_text_input, parent, false));
            case TIME:
                return new ViewHolderTime((inflater.inflate(R.layout.recycler_row_time, parent, false)));
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGeneric holder, int position) {
        Pair<String, ViewType> item = positions.get(position);
        if (item.first == null) {
            throw new IllegalArgumentException("item holds null");
        }
        EntryForDay v; // for logging
        switch (item.second) {
            case BINARY:
                v = rep.getBinaryEntry(item.first);
                holder.init(v);
                break;
            case COUNTER:
                v = rep.getCounterEntry(item.first);
                Log.d(LOGTAG, v.toString());
                holder.init(v);
                break;
            case TEXT:
                v = rep.getTextEntry(item.first);
                holder.init(v);
                break;
            case TIME:
                v = rep.getTimeEntry(item.first);
                holder.init(v);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.second);
        }
        Log.d(LOGTAG, "Binded Entry: " + v.toString());
    }

    @Override
    public int getItemViewType(int position) {
        return positions.get(position).second.asInt();
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public enum ViewType {
        COUNTER(0),
        BINARY(1),
        TEXT(2),
        TIME(3);

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
                    return TEXT;
                case 3:
                    return TIME;
                default:
                    throw new IllegalArgumentException("No EnumType for int: " + i);
            }
        }

        public int asInt() {
            return code;
        }
    }

    // Generic ViewHolder
    public abstract static class ViewHolderGeneric<T> extends RecyclerView.ViewHolder {

        EntryForDay<T> entry;

        ViewHolderGeneric(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void init(@NonNull EntryForDay<T> entry);

    }

    // Subclasses
    public static class ViewHolderBinary extends ViewHolderGeneric<Boolean> {

        private TextView nameTxtView;
        private CheckBox checkBox;

        ViewHolderBinary(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.binary_checkBox);
            checkBox.setOnClickListener(v -> entry.value = checkBox.isChecked());
            nameTxtView = itemView.findViewById(R.id.binary_nameTxtView);
        }

        @Override
        public void init(@NonNull EntryForDay<Boolean> binaryEntry) {
            this.entry = binaryEntry;
            this.nameTxtView.setText(entry.catName);
            checkBox.setChecked(this.entry.value);
        }
    }

    public static class ViewHolderCounter extends ViewHolderGeneric<Integer> {

        TextView nameTxtView, stateTxtView;

        ImageButton plusBttn, minusBttn;

        ViewHolderCounter(@NonNull View itemView) {
            super(itemView);
            nameTxtView = itemView.findViewById(R.id.counter_nameTxtView);
            stateTxtView = itemView.findViewById(R.id.counter_stateTxtView);
            plusBttn = itemView.findViewById(R.id.counter_plusBttn);
            minusBttn = itemView.findViewById(R.id.counter_minusBttn);
            plusBttn.setOnClickListener(event -> updateState(1));
            minusBttn.setOnClickListener(event -> updateState(-1));
        }

        @Override
        public void init(@NonNull EntryForDay<Integer> counterEntry) {
            entry = counterEntry;
            nameTxtView.setText(counterEntry.catName);
            stateTxtView.setText(String.valueOf(counterEntry.value));
        }

        private void updateState(int x) {
            entry.value += x;
            stateTxtView.setText(String.valueOf(entry.value));
        }

    }


    public static class ViewHolderTextInput extends ViewHolderGeneric<String> {
        private TextView nameTextView;
        private ImageView checkView;
        private EditText editText;

        public ViewHolderTextInput(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_txtView);
            editText = itemView.findViewById(R.id.text_textInput);
            checkView = itemView.findViewById(R.id.text_imageview);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()) {
                        checkView.setVisibility(View.VISIBLE);
                        entry.value = s.toString();
                    }
                }
            });
            // Fix for "Focus search returned a view that wasn't able to take focus"
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        @Override
        public void init(@NonNull EntryForDay<String> textEntry) {
            this.entry = textEntry;
            nameTextView.setText(textEntry.catName);
            editText.setText(textEntry.value);
        }

    }


    public class ViewHolderTime extends ViewHolderGeneric<LocalTime> implements TimePickerDialog.OnTimeSetListener {

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
                timePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), " TP");
            });
        }

        @Override
        public void init(@NonNull EntryForDay<LocalTime> timeEntry) {
            this.entry = timeEntry;
            nameTxtView.setText(timeEntry.catName);
            timeInput.setText(timeEntry.value.toString());
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            this.entry.value = LocalTime.of(hourOfDay, minute);
            timeInput.setText(entry.value.toString());
        }
    }
}
