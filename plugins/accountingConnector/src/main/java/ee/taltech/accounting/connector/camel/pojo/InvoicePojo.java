package ee.taltech.accounting.connector.camel.pojo;

import java.time.LocalDateTime;


public class InvoicePojo {

	private String partyIdFrom;
	private String invoiceTypeId;
	private LocalDateTime dueDate;
	private String description;
	private LocalDateTime invoiceDate;
	private String currencyUomId;
	private String statusId;
	private String invoiceId;
	private String partyId;

	public InvoicePojo(String partyIdFrom, String invoiceTypeId, LocalDateTime dueDate, String description, LocalDateTime invoiceDate, String currencyUomId, String statusId, String invoiceId, String partyId) {
		this.partyIdFrom = partyIdFrom;
		this.invoiceTypeId = invoiceTypeId;
		this.dueDate = dueDate;
		this.description = description;
		this.invoiceDate = invoiceDate;
		this.currencyUomId = currencyUomId;
		this.statusId = statusId;
		this.invoiceId = invoiceId;
		this.partyId = partyId;
	}

	public String getPartyIdFrom() {
		return partyIdFrom;
	}

	public void setPartyIdFrom(String partyIdFrom) {
		this.partyIdFrom = partyIdFrom;
	}

	public String getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(String invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDateTime invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getCurrencyUomId() {
		return currencyUomId;
	}

	public void setCurrencyUomId(String currencyUomId) {
		this.currencyUomId = currencyUomId;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
}
