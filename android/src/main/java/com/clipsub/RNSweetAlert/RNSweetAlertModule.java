package com.clipsub.RNSweetAlert;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import cn.pedant.SweetAlert.R; // 🆕 ini penting untuk akses title_text & content_text
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RNSweetAlertModule extends ReactContextBaseJavaModule {
  @Nullable
  private SweetAlertDialog sweetAlertDialog;
  private final ReactApplicationContext reactContext;

  public RNSweetAlertModule(@NonNull final ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @NonNull
  @Override
  public String getName() {
    return "RNSweetAlert";
  }

  @ReactMethod
  public void showAlertWithOptions(final ReadableMap options, final Callback acceptCallback) {
    final Activity activity = getCurrentActivity();
    if (activity == null || activity.isFinishing()) return;

    new Handler(Looper.getMainLooper()).post(() -> {
      sweetAlertDialog = new SweetAlertDialog(activity);

      String type = options.hasKey("style") ? options.getString("style") : "normal";
      String title = options.hasKey("title") ? options.getString("title") : "";
      String contentText = options.hasKey("subTitle") ? options.getString("subTitle") : "";
      String barColor = options.hasKey("barColor") ? options.getString("barColor") : "";
      boolean cancellable = !options.hasKey("cancellable") || options.getBoolean("cancellable");
      String confirmButtonColor = options.hasKey("confirmButtonColor") ? options.getString("confirmButtonColor") : "#AEDEF4";
      String confirmButtonTitle = options.hasKey("confirmButtonTitle") ? options.getString("confirmButtonTitle") : "OK";
      String otherButtonTitle = options.hasKey("otherButtonTitle") ? options.getString("otherButtonTitle") : "";
      String otherButtonColor = options.hasKey("otherButtonColor") ? options.getString("otherButtonColor") : "";
      String fontFamily = options.hasKey("fontFamily") ? options.getString("fontFamily") : ""; // 🆕

      switch (type) {
        case "normal": sweetAlertDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE); break;
        case "error": sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE); break;
        case "success": sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE); break;
        case "warning": sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE); break;
        case "progress": sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE); break;
        default: sweetAlertDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE); break;
      }

      sweetAlertDialog.setTitleText(title);
      sweetAlertDialog.setContentText(contentText);
      sweetAlertDialog.setCancelable(cancellable);
      sweetAlertDialog.setConfirmText(confirmButtonTitle);

      try {
        if (!confirmButtonColor.isEmpty()) {
          sweetAlertDialog.setConfirmButtonBackgroundColor(Color.parseColor(confirmButtonColor));
        }
        if (!barColor.isEmpty()) {
          sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor(barColor));
        }
      } catch (Exception ignored) {}

      if (!otherButtonTitle.isEmpty()) {
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelText(otherButtonTitle);
        try {
          if (!otherButtonColor.isEmpty()) {
            sweetAlertDialog.setCancelButtonBackgroundColor(Color.parseColor(otherButtonColor));
          }
        } catch (Exception ignored) {}
      }

      sweetAlertDialog.setConfirmClickListener(dialog -> {
        acceptCallback.invoke("accepted");
        dialog.dismissWithAnimation();
      });

      sweetAlertDialog.setCancelClickListener(dialog -> dialog.dismissWithAnimation());

      new Handler(Looper.getMainLooper()).post(() -> {
        if (sweetAlertDialog.getWindow() != null) {
          sweetAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
          sweetAlertDialog.getWindow().setDimAmount(0.7f);
        }

        // 🆕 Custom font support
        if (!fontFamily.isEmpty()) {
          try {
            Typeface customFont = Typeface.createFromAsset(
              reactContext.getAssets(),
              "fonts/" + fontFamily
            );
            TextView titleView = sweetAlertDialog.findViewById(R.id.title_text);
            TextView contentView = sweetAlertDialog.findViewById(R.id.content_text);
            if (titleView != null) titleView.setTypeface(customFont);
            if (contentView != null) contentView.setTypeface(customFont);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        sweetAlertDialog.show();
      });
    });
  }

  @ReactMethod
  public void hideSweetAlert() {
    if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
      sweetAlertDialog.dismissWithAnimation();
    }
  }

  @ReactMethod
  public void changeAlertType(String alertType) {
    switch (alertType) {
      case "normal":
        sweetAlertDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        break;
      case "error":
        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        break;
      case "success":
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        break;
      case "warning":
        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        break;
      case "progress":
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        break;
      default:
        sweetAlertDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        break;
    }
  }

  @ReactMethod
  public void showContentText(boolean isShow) {
    sweetAlertDialog.showContentText(isShow);
  }

  @ReactMethod
  public void showCancelButton(boolean isShow) {
    sweetAlertDialog.showCancelButton(isShow);
  }

  @ReactMethod
  public void resetCount() {
    sweetAlertDialog.getProgressHelper().resetCount();
  }

  @ReactMethod
  public void isSpinning(Promise promise) {
    try {
      promise.resolve(sweetAlertDialog.isShowing());
    } catch (Exception e) {
      promise.reject("SweetAlert", e);
    }
  }

  @ReactMethod
  public void spin() {
    sweetAlertDialog.getProgressHelper().spin();
  }

  @ReactMethod
  public void stopSpinning() {
    sweetAlertDialog.getProgressHelper().stopSpinning();
  }

  @ReactMethod
  public void setProgress(float number) {
    sweetAlertDialog.getProgressHelper().setProgress(number);
  }

  @ReactMethod
  public void setInstantProgress(float number) {
    sweetAlertDialog.getProgressHelper().setInstantProgress(number);
  }

  @ReactMethod
  public void setCircleRadius(int radius) {
    sweetAlertDialog.getProgressHelper().setCircleRadius(radius);
  }

  @ReactMethod
  public void setBarWidth(int barWidth) {
    sweetAlertDialog.getProgressHelper().setBarWidth(barWidth);
  }

  @ReactMethod
  public void setBarColor(String barColor) {
    sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor(barColor));
  }

  @ReactMethod
  public void setRimWidth(int rimWidth) {
    sweetAlertDialog.getProgressHelper().setRimWidth(rimWidth);
  }

  @ReactMethod
  public void setSpinSpeed(float spinSpeed) {
    sweetAlertDialog.getProgressHelper().setSpinSpeed(spinSpeed);
  }
}
