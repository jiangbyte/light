#[tokio::main]
async fn main() {
    let listener = tokio::net::TcpListener::bind("0.0.0.0:8081")
        .await
        .expect("bind");
    println!("应用 'apps/admin' 启动，端口 8081");
    axum::serve(listener, triggers_admin::router("admin"))
        .await
        .expect("serve");
}
