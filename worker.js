// Serves docs/ and makes every .vcf a plain text/vcard response (no Content-Disposition),
// which iOS Safari shows as a contact preview instead of offering to download the file.
export default {
  async fetch(request, env) {
    const response = await env.ASSETS.fetch(request);
    if (!response.ok || !new URL(request.url).pathname.endsWith(".vcf")) return response;

    const headers = new Headers(response.headers);
    headers.set("Content-Type", "text/vcard; charset=utf-8");
    headers.delete("Content-Disposition");
    headers.set("Cache-Control", "no-cache");
    return new Response(response.body, { status: response.status, headers });
  },
};
