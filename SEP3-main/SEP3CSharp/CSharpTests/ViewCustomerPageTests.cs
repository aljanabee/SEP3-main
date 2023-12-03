using BlazorClient.Pages;

namespace CSharpTests;

public class ViewCustomerPageTests {
    // True statements
    [TestCase("299141@via.dk", "12345678", "Åse Østergård", "Something Street 32 8700", ExpectedResult = true)]
    [TestCase("299141@via.dk", "12345671", "Bob Bobson", "Something Street 32 8700", ExpectedResult = true)]
    [TestCase("299141@via.dk", "12345672", "Bob", "Something Street 32 8700", ExpectedResult = true)]

    // Address validation
    [TestCase("299141@via.dk", "12345675", "Bob", "", ExpectedResult = false)]


    // Email validation
    [TestCase("", "12345673", "Bob", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("2991!1@via.dk", "12345676", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141@v!a.dk", "12345677", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141@via", "12345678", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141via.dk", "12345679", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]

    //Phone number validation
    [TestCase("299141@via.dk", "", "Bob", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141via.dk", "123456ff", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141via.dk", "1234567", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141via.dk", "123456789", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141via.dk", "!2345678", "Bob Bobson", "Something Street 32 8700", ExpectedResult = false)]

    //Fullname validation
    [TestCase("299141@via.dk", "12345674", "", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141@via.dk", "12345678", "Bobby123", "Something Street 32 8700", ExpectedResult = false)]
    [TestCase("299141@via.dk", "12345678", "Bob Bobson!", "Something Street 32 8700", ExpectedResult = false)]

    [Test]
    public bool ValidateCustomerTest(string mail, string phoneNumber, string fullName, string address) {
        ViewCustomers create = new ViewCustomers();

        bool valipNo = create.ValidatePhone(phoneNumber);
        bool valiMail = create.ValidateMail(mail);
        bool valiIfNull = create.ValidateFullName(fullName);
        bool valiAddress = create.ValidateAddress(address);

        bool result;

        if (!valiIfNull == true) {
            result = false;
            return result;
        }
        else {
            if (!valiMail == true) {
                result = false;
                return result;
            }

            if (!valipNo == true) {
                result = false;
                return result;
            }

            if (!valiAddress == true) {
                result = false;
                return result;
            }
        }


        result = true;
        return result;
    }
}
