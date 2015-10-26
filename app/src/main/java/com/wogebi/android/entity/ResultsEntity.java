package com.wogebi.android.entity;

import java.util.List;

public class ResultsEntity<T extends BaseEntity> extends BaseEntity
{
    private int code = 0;    // 返回代码
    private String msg = "";    // 返回消息
    private List<T> data = null; // 返回数据集

    public ResultsEntity()
    {
        super();
    }

    public ResultsEntity(int code, String msg, List<T> data)
    {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }

	/*public static <TT extends BaseEntity> JsonResultsEntity<TT> getJsonResultsEntity(String json, Class<TT> clazz){
        L.i("JsonResolveUtils.getUser", json);

		JsonResultsEntity<TT> ret = new JsonResultsEntity<TT>();
		try
		{

			JSONObject obj = new JSONObject(json);
			ret.setCode(obj.optInt("code"));
			ret.setMsg(obj.optString("msg"));

			if(obj.has("data")){

				Gson gson = new Gson();
				List<TT> d = null;
				d = gson.fromJson(obj.getString("data"), new TypeToken<List<TT>>(){}.getType());
				ret.setData(d);
			}
		}
		catch (JSONException e)
		{
		}
		return ret;
	}*/

    public static void aa()
    {
    }
}
