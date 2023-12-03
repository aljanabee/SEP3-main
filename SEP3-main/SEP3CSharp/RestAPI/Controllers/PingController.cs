using Application.LogicInterfaces;
using gRPC;
using Microsoft.AspNetCore.Mvc;

namespace RestAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class PingController : ControllerBase {
    private readonly IPingLogic _pingLogic;

    public PingController(IPingLogic pingLogic) {
        _pingLogic = pingLogic;
    }

    [HttpGet]
    public async Task<ActionResult<long[]>> GetPingAsync()
    {
        try
        {
            PingResponse created = await _pingLogic.PingAsync();
            return Ok(new long[] { created.OriginDate, created.ReturnDate });
        }

        catch (Exception e)
        {
            Console.WriteLine(e);
            return StatusCode(500, e.Message);
        }
    }
}