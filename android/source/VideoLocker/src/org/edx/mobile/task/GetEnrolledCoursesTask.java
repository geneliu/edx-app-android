package org.edx.mobile.task;

import java.util.ArrayList;

import org.edx.mobile.exception.AuthException;
import org.edx.mobile.http.Api;
import org.edx.mobile.model.api.EnrolledCoursesResponse;

import android.content.Context;

public abstract class GetEnrolledCoursesTask extends Task<ArrayList<EnrolledCoursesResponse>> {

    public GetEnrolledCoursesTask(Context context) {
        super(context);
    }

    @Override
    protected ArrayList<EnrolledCoursesResponse> doInBackground(Object... params) {
        try { 
            Api api = new Api(context);

            // TODO Handle below response, navigate to login?
            /* 
             * 07-08 10:40:23.278: D/Api(1119): getEnrolledCourses={"detail": "Authentication credentials were not provided."}
             */
            
            // return instant cached data
            try {
                final ArrayList<EnrolledCoursesResponse> list = api
                        .getEnrolledCourses(true);
                if (list != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            onFinish(list);
                            stopProgress();
                        }
                    });
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            
            return api.getEnrolledCourses();
        } catch(AuthException ex) {
            handle(ex);
        } catch(Exception ex) {
            handle(ex);
        }
        return null;
    }
}
