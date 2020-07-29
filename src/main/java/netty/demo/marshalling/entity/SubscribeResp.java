package netty.demo.marshalling.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:27
 */
@Data
@ToString
public class SubscribeResp implements Serializable {

    private static final long serialVersionUID = 3558725849157507552L;

    private int subReqID;

    private int respCode;

    private String desc;

}
