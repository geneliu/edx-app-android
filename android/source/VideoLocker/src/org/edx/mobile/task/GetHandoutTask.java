package org.edx.mobile.task;

import org.edx.mobile.http.Api;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.model.api.HandoutModel;

import android.content.Context;

public abstract class GetHandoutTask extends Task<HandoutModel> {

    public GetHandoutTask(Context context) {
        super(context);
    }

    @Override
    protected HandoutModel doInBackground(Object... params) {
        try {
            EnrolledCoursesResponse enrollment = (EnrolledCoursesResponse) params[0];
            if(enrollment!=null){
                Api api = new Api(context);

                try {
                    // return instant data from cache
                    final HandoutModel model = api.getHandout
                            (enrollment.getCourse().getCourse_handouts(), true);
                    if (model != null) {
                        handler.post(new Runnable() {
                            public void run() {
                                onFinish(model);
                                stopProgress();
                            }
                        });
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                return api.getHandout(enrollment.getCourse()
                        .getCourse_handouts(), false);
            }
        } catch (Exception ex) {
            handle(ex);
        }
        return null;
    }

}
