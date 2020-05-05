On toetatud järgmised query'd

1.  	paymentgatewaypayflowpro_ : [PaymentGatewayPayflowPro]
2.  	paymentgatewaypayflowpro(paymentGatewayConfigId : String) : PaymentGatewayPayflowPro
3.  	deletepaymentgatewaypayflowpro(paymentGatewayConfigId : String) : PaymentGatewayPayflowPro
4.  	postpaymentgatewaypayflowpro_(redirectUrl : String, createdStamp : Long, cancelReturnUrl : String, enableTransmit : String, timeout : Long, proxyPort : Long, proxyLogon : String, certsPath : String, vendor : String, logFileName : String, hostPort : Long, preAuth : String, proxyAddress : String, returnUrl : String, maxLogFileSize : Long, lastUpdatedStamp : Long, createdTxStamp : Long, proxyPassword : String, lastUpdatedTxStamp : Long, checkAvs : String, stackTraceOn : String, userId : String, checkCvv2 : String, partner : String, hostAddress : String, pwd : String, loggingLevel : Long) : PaymentGatewayPayflowPro
5.  	postpaymentgatewaypayflowpro(redirectUrl : String, paymentGatewayConfigId : String, createdStamp : Long, cancelReturnUrl : String, enableTransmit : String, timeout : Long, proxyPort : Long, proxyLogon : String, certsPath : String, vendor : String, logFileName : String, hostPort : Long, preAuth : String, proxyAddress : String, returnUrl : String, maxLogFileSize : Long, lastUpdatedStamp : Long, createdTxStamp : Long, proxyPassword : String, lastUpdatedTxStamp : Long, checkAvs : String, stackTraceOn : String, userId : String, checkCvv2 : String, partner : String, hostAddress : String, pwd : String, loggingLevel : Long) : PaymentGatewayPayflowPro
6.  	putpaymentgatewaypayflowpro(redirectUrl : String, paymentGatewayConfigId : String, createdStamp : Long, cancelReturnUrl : String, enableTransmit : String, timeout : Long, proxyPort : Long, proxyLogon : String, certsPath : String, vendor : String, logFileName : String, hostPort : Long, preAuth : String, proxyAddress : String, returnUrl : String, maxLogFileSize : Long, lastUpdatedStamp : Long, createdTxStamp : Long, proxyPassword : String, lastUpdatedTxStamp : Long, checkAvs : String, stackTraceOn : String, userId : String, checkCvv2 : String, partner : String, hostAddress : String, pwd : String, loggingLevel : Long) : PaymentGatewayPayflowPro

Kus:

1.   "entity"_ tagastab kõik entity'd.
2.   "entity"(PK) tagastab entity primary key järgi.
3.   delete"entity"(PK) kustutab entity ja tagastab terve kustutatud entity.
4.   post"entity"(PK, fields) lisab uue entity ja tagastab lisatud entity.
5.   post"entity"_(fields) lisab uue entity, genereerib uue primary key ning tagastab lisatud entity.
6.   put"entity"(PK, fields) uuendab eksisteerivat entity't, mis otsitakse primary key'de järgi.

TL;DR - _ lõpus tähendab, et primary key'd pole vaja kaasa anda ning kõiki fielde pole post/put tehes kaasa anda - defaultib null'iks


Näide komplekspäringust

```
{
  party(partyId: "DemoEmployee") {
    partyId

    _toOne_PartyGroup {
      groupName
      groupNameLocal
      officeSiteName
      annualRevenue
      numEmployees
      tickerSymbol
    }
    
    description
    
    _toOne_Uom {
      abbreviation
    }
    
    externalId
    
    statusId
    
    lastUpdatedStamp
    lastModifiedDate
    createdStamp
    createdDate # party creation
    
    _toOne_PartyType {
      partyTypeId
      parentTypeId
      description
    } 
    
    _toMany_PartyRole {
      roleTypeId
    }
  }
}
```
