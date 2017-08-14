package com.iranplanner.tourism.iranplanner.ui.activity.confirmHotelReservation;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.standard.DataTransferInterface;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import entity.ReqLodgingReservation;
import tools.CustomDialogNumberPicker;

/**
 * Created by Hoda on 10/01/2017.
 */
public class HotelReservationConfirmListAdapter extends RecyclerView.Adapter<HotelReservationConfirmListAdapter.ViewHolder>/* implements View.OnClickListener*/ {
    Context context;
    int rowLayout;
    DataTransferInterface dtInterface;
    LayoutInflater inflater;
    List<entity.ResultRoom> resultRooms;
    Map<Integer, Integer> selectedRooms;
    List<ReqLodgingReservation> reqLodgingReservationList;
    Integer roomNumberSelected;
    Integer roomPricefinal;
    Integer PriceHalfboardIn;
    Integer PriceHalfboardOut, roomCapacityExtra, priceAddPeople, priceHalfInPrice, roomCapacityExtraPrice,
            priceHalfOutPrice;
    int durationTravel;
    Date startOfTravel;
    int selectAddPeople = 0;
    private Activity activity;
    private GoBackInterface listener;

    public HotelReservationConfirmListAdapter(int durationTravel, Date startOfTravel, Activity a, DataTransferInterface dtInterface, Context context, int rowLayout, Map<Integer, Integer> selectedRooms, List<entity.ResultRoom> ResultRooms, GoBackInterface listener) {
        this.resultRooms = ResultRooms;
        this.context = context;
        this.rowLayout = rowLayout;
        this.selectedRooms = selectedRooms;
        this.activity = a;
        this.dtInterface = dtInterface;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.startOfTravel = startOfTravel;
        this.durationTravel = durationTravel;
        this.listener = listener;

    }

    @Override
    public HotelReservationConfirmListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.from(viewGroup.getContext()).inflate(R.layout.content_room_reservation_fill, viewGroup, false);
        return new HotelReservationConfirmListAdapter.ViewHolder(view);
    }

    private void setDefaultValue(ViewHolder viewHolder, int position) {
        roomPricefinal = (resultRooms.get(position).getRoom_price_final() != null) ? Integer.valueOf(resultRooms.get(position).getRoom_price_final()) : 1;
        PriceHalfboardIn = (resultRooms.get(position).getRoomPriceHalfboardIn() != null) ? Integer.valueOf(resultRooms.get(position).getRoomPriceHalfboardIn()) : 0;
        PriceHalfboardOut = (resultRooms.get(position).getRoomPriceHalfboardOut() != null) ? Integer.valueOf(resultRooms.get(position).getRoomPriceHalfboardOut()) : 0;
        roomCapacityExtra = (resultRooms.get(position).getRoomCapacityExtra() != null) ? Integer.valueOf(resultRooms.get(position).getRoomCapacityExtra()) : 0;
        roomCapacityExtraPrice = (resultRooms.get(position).getRoomPriceAdPeople() != null) ? Integer.valueOf(resultRooms.get(position).getRoomPriceAdPeople()) : 0;
        priceAddPeople = (resultRooms.get(position).getPriceAddPeople() != null) ? Integer.valueOf(resultRooms.get(position).getPriceAddPeople()) : 0;
        priceHalfInPrice = (resultRooms.get(position).getHalfIn() != null && resultRooms.get(position).getHalfIn()) ? Integer.valueOf(resultRooms.get(position).getRoomPriceHalfboardIn()) : 0;
        priceHalfOutPrice = (resultRooms.get(position).getHalfOut() != null && resultRooms.get(position).getHalfOut()) ? Integer.valueOf(resultRooms.get(position).getRoomPriceHalfboardOut()) : 0;
        viewHolder.roomType.setText(resultRooms.get(position).getRoomTitle());
        viewHolder.txtPrice.setText(roomPricefinal * durationTravel + "");
        viewHolder.txtAddPeople.setText(roomCapacityExtraPrice * durationTravel * selectAddPeople + "");
        viewHolder.edtHeadNameReservation.setText(resultRooms.get(position).getHeadName());
        viewHolder.txtDiscount.setText(resultRooms.get(position).getRoomPricePromotion());
        viewHolder.endPrice.setText((roomPricefinal * durationTravel) - priceHalfOutPrice - priceHalfInPrice + (roomCapacityExtraPrice * durationTravel * selectAddPeople) + "تومان");

//        changeBtnOKConfirm(viewHolder, position);


    }

    private void changeBtnOKConfirm(ViewHolder viewHolder, int position) {
        resultRooms.get(position).setOkConfirmChange(false);

//        viewHolder.txtOkRoom.setBackground(context.getResources().getDrawable(R.drawable.button_corner_green_stroke));
        viewHolder.txtOkRoom.setText("تایید");

    }

    private void setVisibleHalfBoard(ViewHolder viewHolder, int position) {
        if (resultRooms.get(position).getRoomPriceHalfboardIn() != null || resultRooms.get(position).getRoomPriceHalfboardOut() != null) {
            viewHolder.holder.setVisibility(View.VISIBLE);
        }
        if (resultRooms.get(position).getHalfIn() != null && resultRooms.get(position).getHalfOut() != null && resultRooms.get(position).getHalfIn() && resultRooms.get(position).getHalfOut()) {
            viewHolder.txthalfInPrice.setText(priceHalfOutPrice + priceHalfInPrice + "");
        } else if (resultRooms.get(position).getHalfIn() != null && resultRooms.get(position).getHalfIn()) {
            viewHolder.txthalfInPrice.setText(priceHalfInPrice + "");
        } else if (resultRooms.get(position).getHalfOut() != null && resultRooms.get(position).getHalfOut()) {
            viewHolder.txthalfInPrice.setText(priceHalfOutPrice + "");
        } else {
            viewHolder.txthalfInPrice.setText("");
        }
    }

    private void setChangeAddPerson(final ViewHolder viewHolder, final int position) {
        viewHolder.addPerHolderHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNumber(viewHolder, position, roomCapacityExtra, viewHolder.txtaddPersonValue, null, " نفراضافه ");
            }
        });
    }

    private void setChangeNationalityPerson(final ViewHolder viewHolder, final int position) {
        viewHolder.NationalHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogString(viewHolder, position, 1, viewHolder.txtNationalityValue, new String[]{"ایرانی", "خارجی"}, " نفراضافه ");
            }
        });
    }

    private void setChangeHalfBoard(final ViewHolder viewHolder, final int position) {
        viewHolder.checkHalfIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                resultRooms.get(position).setHalfIn(b);
                changeBtnOKConfirm(viewHolder, position);

                notifyDataSetChanged();
            }
        });
        viewHolder.checkHalfOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                resultRooms.get(position).setHalfOut(b);
                changeBtnOKConfirm(viewHolder, position);
                notifyDataSetChanged();
            }
        });
    }

    private void setConfirmAllChanges(final ViewHolder viewHolder, final int position) {
        viewHolder.txtOkRoom.setText("تایید نهایی");

    }

    private void deleteRoom(int position) {
        resultRooms.remove(position);
        notifyDataSetChanged();
        if (resultRooms.size() == 0) {
            listener.clearList();

        }
    }


    public interface GoBackInterface {
        public void clearList();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        resultRooms.get(position).setSelectedNationality(0 + "");
        setVisibleHalfBoard(viewHolder, position);
        setDefaultValue(viewHolder, position);
        setChangeAddPerson(viewHolder, position);
        setChangeNationalityPerson(viewHolder, position);
        setChangeHalfBoard(viewHolder, position);
//        setConfirmAllChanges(viewHolder, position);
        viewHolder.txtOkRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean a=checkInfo(viewHolder, position);

                if (resultRooms.get(position).getOkConfirmChange()!=null && !resultRooms.get(position).getOkConfirmChange() && a) {
                    resultRooms.get(position).setHeadName(viewHolder.edtHeadNameReservation.getText().toString());
                    resultRooms.get(position).setHeadLastName(viewHolder.edtHeadLastNameReservation.getText().toString());
                    resultRooms.get(position).setOkConfirmChange(true);
                    notifyDataSetChanged();
                    Log.e("dd","dd");
                }

            }
        });
        viewHolder.roomDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoom(position);
            }
        });
    }

    private boolean checkInfo(ViewHolder viewHolder, int position) {
        boolean validate = true;
        if (!viewHolder.edtHeadNameReservation.getText().toString().equals("")) {
            resultRooms.get(position).setHeadName(viewHolder.edtHeadNameReservation.getText().toString());
            changeBtnOKConfirm(viewHolder, position);
        } else if (viewHolder.edtHeadNameReservation.getText().toString().equals("")) {
            viewHolder.edtHeadNameReservation.setError("نام سرپرست وارد نشده است");
            validate = false;
        }
        if (!viewHolder.edtHeadLastNameReservation.getText().toString().equals("")) {
            resultRooms.get(position).setHeadLastName(viewHolder.edtHeadLastNameReservation.getText().toString());
            changeBtnOKConfirm(viewHolder, position);
        } else if (viewHolder.edtHeadLastNameReservation.getText().toString().equals("")) {
            viewHolder.edtHeadLastNameReservation.setError("نام خانوادگی سرپرست وارد نشده است");
            validate = false;
        }
        return validate;
    }

    @Override
    public int getItemCount() {
        return resultRooms.size();
    }

    private void showDialogNumber(final ViewHolder viewHolder, final int position, int number, final TextView txtaddPersonValue, String[] strings, String title) {
        CustomDialogNumberPicker cdd = new CustomDialogNumberPicker(activity, number, 0, title, strings);
        cdd.show();
        cdd.setDialogResult(new CustomDialogNumberPicker.OnDialogNumberPick() {
            @Override
            public void finish(int result) {
                selectAddPeople = result;
                txtaddPersonValue.setText(result + "نفر اضافه");
                resultRooms.get(position).setSelectedAddNumbers(result + "");
                changeBtnOKConfirm(viewHolder, position);
                notifyDataSetChanged();
            }
        });
    }

    private void showDialogString(final ViewHolder viewHolder, final int position, int number, final TextView txtaddPersonValue, String[] strings, String title) {
        CustomDialogNumberPicker cdd = new CustomDialogNumberPicker(activity, number, 0, title, strings);
        cdd.show();
        cdd.setDialogResult(new CustomDialogNumberPicker.OnDialogNumberPick() {
            @Override
            public void finish(int result) {
                selectAddPeople = result;
                txtaddPersonValue.setText((result == 0) ? "ایرانی" : "خارجی");
                resultRooms.get(position).setSelectedNationality(result + "");
                changeBtnOKConfirm(viewHolder, position);
                notifyDataSetChanged();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.roomType)
        TextView roomType;
        @InjectView(R.id.txtOkRoom)
        TextView txtOkRoom;
        @InjectView(R.id.txtPrice)
        TextView txtPrice;
        @InjectView(R.id.txtAddPeople)
        TextView txtAddPeople;

        @InjectView(R.id.checkHalfIn)
        CheckBox checkHalfIn;
        @InjectView(R.id.checkHalfOut)
        CheckBox checkHalfOut;
        @InjectView(R.id.txthalfInPrice)
        TextView txthalfInPrice;
        @InjectView(R.id.txtaddPersonValue)
        TextView txtaddPersonValue;
        @InjectView(R.id.txtNationalityValue)
        TextView txtNationalityValue;
        @InjectView(R.id.txtDiscount)
        TextView txtDiscount;
        @InjectView(R.id.endPrice)
        TextView endPrice;
        @InjectView(R.id.roomDelete)
        TextView roomDelete;


        @InjectView(R.id.edtHeadNameReservation)
        EditText edtHeadNameReservation;
        @InjectView(R.id.edtHeadLastNameReservation)
        EditText edtHeadLastNameReservation;
        @InjectView(R.id.holderNationality)
        RelativeLayout holderNationality;
        @InjectView(R.id.addPerHolderHolder)
        RelativeLayout addPerHolderHolder;
        @InjectView(R.id.NationalHolder)
        RelativeLayout NationalHolder;
        @InjectView(R.id.holder)
        RelativeLayout holder;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }


}


