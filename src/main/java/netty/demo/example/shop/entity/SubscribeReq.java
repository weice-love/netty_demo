package netty.demo.example.shop.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:27
 */
@Data
@ToString
public class SubscribeReq implements Serializable {

    private static final long serialVersionUID = -922718607516317821L;

    private int subReqID;

    private String userName;

    private String productName;

    private String phoneNumber;

    private String address;

}
