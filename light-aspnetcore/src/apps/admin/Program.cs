using Triggers.Admin;

var app = WebBootstrap.BuildApp(args, "admin", 8081);
Console.WriteLine("应用 'apps/admin' 启动，端口 8081");
app.Run();
