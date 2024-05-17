package com.data.filtro.model.payment.vnpay;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VNPIPNResponse {
    private String RspCode;
    private String Message;
}
