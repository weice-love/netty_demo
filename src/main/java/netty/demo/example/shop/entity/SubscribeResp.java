package netty.demo.example.shop.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

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
