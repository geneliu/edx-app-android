package org.edx.mobile.task;

import java.util.Map;

import org.edx.mobile.http.Api;
import org.edx.mobile.model.api.SectionEntry;

import android.content.Context;

public abstract class GetCourseHierarchyTask extends
Task<Map<String, SectionEntry>> {

    public GetCourseHierarchyTask(Context context) {
        super(context);
    }

    protected Map<String, SectionEntry> doInBackground(Object... params) {
        try {
            String courseId = (String) (params[0]);
            //String url = (String) (params[1]);
            if(courseId!=null){
                Api api = new Api(context);
                try {
                    // return instant data from cache
                    final Map<String, SectionEntry> map = api.getCourseHierarchy(courseId, true);
                    if (map != null) {
                        handler.post(new Runnable() {
                            public void run() {
                                onFinish(map);
                                stopProgress();
                            }
                        });
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                // return live data
                return api.getCourseHierarchy(courseId);
            }
        } catch (Exception ex) {
            handle(ex);
        }
        return null;
    }
}
