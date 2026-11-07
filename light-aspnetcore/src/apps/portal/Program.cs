using Triggers.Portal;

var app = WebBootstrap.BuildApp(args, "portal", 8080);
Console.WriteLine("应用 'apps/portal' 启动，端口 8080");
app.Run();
