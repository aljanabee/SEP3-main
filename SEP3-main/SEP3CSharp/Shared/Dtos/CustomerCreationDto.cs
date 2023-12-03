namespace Shared.Dtos;

public class CustomerCreationDto
{
    public string FullName { get; set; }
    public string PhoneNo { get; set; }
    public string Address { get; set; }
    public string Mail { get; set; }

    public CustomerCreationDto(string fullName, string phoneNo, string address, string mail)
    {
        FullName = fullName;
        PhoneNo = phoneNo;
        Address = address;
        Mail = mail;
    }
}