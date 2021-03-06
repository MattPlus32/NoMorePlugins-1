/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package plugin.nomore.qolclicks.object;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import plugin.nomore.qolclicks.AIOConfig;
import plugin.nomore.qolclicks.AIOPlugin;
import plugin.nomore.qolclicks.object.builder.HighlightingObject;
import plugin.nomore.nmputils.api.RenderAPI;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class  ObjectHighlightingOverlay extends Overlay
{

    @Inject
    private RenderAPI render;

    @Inject
    private AIOConfig config;

    @Inject
    private AIOPlugin plugin;

    @Inject
    private ObjectMethods objectMethods;

    @Inject
    private Client client;

    @Inject
    public ObjectHighlightingOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }
        if (config.enableObjectMarkers())
        {
            renderObjects(graphics, player);
        }
        return null;
    }

    private void renderObjects(Graphics2D graphics, Player player)
    {
        List<HighlightingObject> objectsToHighlight = objectMethods.getObjectToHighlight();
        for (HighlightingObject highlightingObject : objectsToHighlight)
        {
            TileObject object = highlightingObject.getObject();
            Color color = highlightingObject.getColor();
            if (object == null)
            {
                continue;
            }

            if (player.getWorldLocation().getPlane() != highlightingObject.getPlane())
            {
                return;
            }

            if (config.objectLineOfSight()
                    && !objectMethods.doesPlayerHaveALineOfSightToObject(player, object))
            {
                return;
            }

            switch (config.objectRenderStyle())
            {
                case BOX:
                    render.renderObjectCentreBox(graphics, object, color, config.objectIndicatorSize());
                    showMouseOverlay(graphics, object, color);
                    break;
                case HULL:
                    render.clickbox(graphics, client.getMouseCanvasPosition(), object.getClickbox(), color);
                    showMouseOverlay(graphics, object, color);
                    break;
                case CLICKBOX:
                    render.hull(graphics, object.getClickbox(), color);
                    showMouseOverlay(graphics, object, color);
                    break;
                case FILL:
                    render.fill(graphics, object.getClickbox(), color);
                    showMouseOverlay(graphics, object, color);
                    break;
                case TILE_OUTLINE:
                    render.outline(graphics, object.getCanvasTilePoly(), color);
                    showMouseOverlay(graphics, object, color);
                    break;
                case TILE_FILLED:
                    render.fill(graphics, object.getCanvasTilePoly(), color);
                    showMouseOverlay(graphics, object, color);
                    break;
            }

        }
    }

    private void showMouseOverlay(Graphics2D graphics, TileObject object, Color color)
    {
        if (config.objectDisplayMouseHoveringIndicator()
                && render.isMouseHoveringOver(object.getClickbox(), client.getMouseCanvasPosition()))
        {
            render.canvasIndicator(graphics, render.getCanvasIndicatorLocation(config.objectMouseHoveringIndicatorLocation()), color);
        }
    }

}
