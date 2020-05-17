////// header //////

//// Invoice Type: _toOne_InvoiceType.description // or maybe ID converted
//// Description: description
//// From Party ID: _toOne_FromParty._toOne_PartyGroup.groupName concat [partyIdFrom]
//// Role Type ID: roleTypeId
//// Invoice Date: invoiceDate // to date yyyy-mm-dd
//// Total: // sum of item totals
//// Reference Num: referenceNumber
//// Status: _toOne_StatusItem.description
//// Invoice Message: invoiceMessage
//// To Party ID: _toOne_Party._toOne_PartyGroup.groupName concat [partyId]
//// Billing Account ID: billingAccountId
//// Due Date: dueDate // to date yyyy-mm-dd
//// Date Paid: paidDate // to date yyyy-mm-dd


////// status //////
/// base: _toMany_InvoiceStatus

//// Status Date: statusDate // to date yyyy-mm-dd
//// Status: _toOne_StatusItem.description
//// Change By User Login ID: changeByUserLoginId

////// roles //////

// TODO: no existing examples


////// applied payments x open y //////
///// x = sum of applied, y = header Total - x
/// base: _toMany_PaymentApplication

//// Item No: // no clue
//// Product Id: // no clue
//// Description: // no clue
//// Total: // not sure if amount applied or something else
//// Payment ID: paymentId
//// Amount Applied: amountApplied


////// terms //////

// TODO: no clue where they come from


////// items //////
/// base: _toMany_InvoiceItem

//// Item No: invoiceItemSeqId
//// Invoice Item Type: _toOne_InvoiceItemType.description
//// Override Gl Account ID: overrideGlAccountId
//// Override Org Party ID: overrideOrgPartyId
//// Inventory Item ID: inventoryItemId
//// Product Id: productId
//// Product Feature ID: productFeatureId
//// Parent Invoice ID: parentInvoiceId
//// Parent Invoice Item Seq ID: parentInvoiceItemSeqId
//// UOM: _toOne_Uom.abbreviation
//// Taxable Flag: taxableFlag
//// Quantity: quantity
//// Unit Price: amount // not 100% sure
//// Description: description
//// tax authority party: taxAuthPartyId
//// Tax Auth Geo ID: taxAuthGeoId
//// Tax Authority Rate Seq ID: taxAuthorityRateSeqId
//// Sales Opportunity ID: salesOpportunityId
//////// Order ID: ???? data display toOne, aga andmed toMany?
//////// Order Item Seq ID: ???? data display toOne, aga andmed toMany?
//// Total: // quantity * amount


////// transactions //////
/// parent2: Invoice itself OR base._toOne_AcctgTrans._toOne_Invoice
/// parent: _toMany_AcctgTrans OR base._toOne_AcctgTrans
/// base: _toMany_AcctgTrans._toMany_AcctgTransEntry

//// Acctg Trans ID: acctgTransId
//// Acctg Trans Entry Seq ID: acctgTransEntrySeqId
//// Is Posted: parent.isPosted
//// Fiscal Gl Type ID: parent.glFiscalTypeId
//// Acctg Trans Type ID: parent2._toOne_InvoiceType.description // or maybe ID converted
//// Transaction Date: parent.transactionDate
//// Posted Date: parent.postedDate
//// Gl Journal ID: parent.glJournalId
//// Trans Type Description: parent2._toOne_InvoiceType.description
//// Payment ID: parent.paymentId
//// Fixed Asset ID: parent.fixedAssetId
//// Description: description
//// Gl Account ID: glAccountId
//// Product ID: productId
//// Debit Credit Flag: debitCreditFlag
//// Amount: amount
//// Orig Amount: origAmount
//// Organization Party ID: organizationPartyId
//// Gl Account Type: _toOne_GlAccount._toOne_GlAccountType.description
//// Account Code: _toOne_GlAccount.accountCode
//// Account Name: _toOne_GlAccount.accountName
//// GL Account Class: _toOne_GlAccount._toOne_GlAccountClass.description
//// Party: _toOne_Party._toOne_PartyGroup.groupName
//// Reconcile Status ID: _toOne_StatusItem.description // or statusCode converted
//// Acctg Trans Entry Type ID: acctgTransEntryTypeId