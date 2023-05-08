using Microsoft.AspNetCore.Mvc;
using WebApiAjax.Models;

namespace WebApiAjax.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MathController : ControllerBase
    {
        // GET api/math
        [HttpGet]
        public CalcResult Calculate(int x, int y)
        {
            return new CalcResult()
            {
                Sum = x + y,
                Difference = x - y,
                Product = x * y,
                Quotient = x / y
            };
        }
    }
}
